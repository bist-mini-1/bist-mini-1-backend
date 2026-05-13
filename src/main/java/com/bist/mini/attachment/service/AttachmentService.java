package com.bist.mini.attachment.service;

import com.bist.mini.attachment.dao.AttachmentDao;
import com.bist.mini.attachment.dto.AttachmentUploadResponse;
import com.bist.mini.attachment.entity.Attachment;
import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private static final String TYPE_IMAGE = "IMAGE";
    private static final String TYPE_FILE = "FILE";
    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;
    private static final long MAX_INLINE_IMAGE_SIZE = 10L * 1024 * 1024;

    private final AttachmentDao attachmentDao;

    @Transactional
    public List<AttachmentUploadResponse> uploadFiles(List<MultipartFile> files, String uploadTypeRaw) {
        String uploadType = normalizeUploadType(uploadTypeRaw);
        validateFiles(files, uploadType);

        List<AttachmentUploadResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                byte[] fileData = file.getBytes();
                String extension = extractExtension(file.getOriginalFilename());
                if (extension.startsWith(".")) {
                    extension = extension.substring(1);
                }

                // DB에 직접 저장 (post_id는 null, 게시글 저장 시 업데이트됨)
                Attachment attachment = Attachment.builder()
                        .post_id(null)
                        .original_name(file.getOriginalFilename())
                        .file_size(file.getSize())
                        .file_type(file.getContentType())
                        .extension(extension.isEmpty() ? null : extension)
                        .upload_type(uploadType)
                        .file_data(fileData)
                        .download_count(0L)
                        .is_deleted("N")
                        .created_at(LocalDateTime.now())
                        .build();

                attachmentDao.insert(attachment);

                // 저장된 attachment의 id를 활용한 URL 생성
                boolean isImage = isImageContentType(file.getContentType());
                String imageUrl = "/api/attachments/" + attachment.getAttachment_id() + "/image";
                String downloadUrl = "/api/attachments/" + attachment.getAttachment_id() + "/download";
                String fileUrl = isImage ? imageUrl : downloadUrl;
                responses.add(AttachmentUploadResponse.builder()
                        .attachmentId(attachment.getAttachment_id())
                        .originalName(file.getOriginalFilename())
                        .fileUrl(fileUrl)
                    .downloadUrl(downloadUrl)
                    .image(isImage)
                    .markdown(buildMarkdownSnippet(attachment.getAttachment_id(), file.getOriginalFilename(), file.getContentType(), downloadUrl, imageUrl, file.getSize()))
                        .uploadType(uploadType)
                        .fileSize(file.getSize())
                        .build());
            } catch (IOException e) {
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        return responses;
    }

    public Attachment getAttachment(Long id) {
        List<Attachment> list = attachmentDao.findByIds(List.of(id));
        if (list.isEmpty()) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }
        return list.get(0);
    }

    @Transactional
    public String syncPostAttachments(Long postId, String thumbnailUrl, List<String> tempAttachmentIdsRaw, List<String> tempInlineImageIdsRaw, String content) {
        // 이제 content에서 attachment_id를 자동으로 파싱합니다.
        // 패턴: /api/attachments/{id}/image 또는 /api/attachments/{id}/download
        List<Long> attachmentIdsInContent = extractAttachmentIdsFromContent(content);
        
        // content의 attachment_id들을 이 post와 연결
        if (!attachmentIdsInContent.isEmpty()) {
            attachmentDao.bindToPost(postId, attachmentIdsInContent);
        }
        
        // 이전에 post와 연결된 attachment 중에 content에 없는 것들은 삭제
        List<Attachment> currentAttachments = attachmentDao.findActiveByPostId(postId);
        List<Long> toDelete = new ArrayList<>();
        for (Attachment att : currentAttachments) {
            if (!attachmentIdsInContent.contains(att.getAttachment_id())) {
                toDelete.add(att.getAttachment_id());
            }
        }
        if (!toDelete.isEmpty()) {
            attachmentDao.softDeleteByIds(toDelete);
        }

        // 썸네일 설정 처리
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            Long thumbnailId = extractIdFromUrl(thumbnailUrl);
            if (thumbnailId != null) {
                // 해당 포스트의 기존 썸네일들을 모두 IMAGE로 초기화
                attachmentDao.resetThumbnailTypeByPostId(postId);
                // 선택된 이미지를 THUMBNAIL로 설정
                attachmentDao.updateType(thumbnailId, "THUMBNAIL");
            }
        }
        
        return content;
    }
    
    private Long extractIdFromUrl(String url) {
        if (url == null) return null;
        Pattern pattern = Pattern.compile("/?api/attachments/(\\d+)/(?:image|download)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            try {
                return Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    private List<Long> extractAttachmentIdsFromContent(String content) {
        List<Long> ids = new ArrayList<>();
        if (content == null || content.isEmpty()) {
            return ids;
        }
        
        // 정규표현식으로 /api/attachments/{id}/image 또는 /api/attachments/{id}/download 패턴 찾기
        Pattern pattern = Pattern.compile("/?api/attachments/(\\d+)/(?:image|download)");
        Matcher matcher = pattern.matcher(content);
        
        while (matcher.find()) {
            try {
                Long id = Long.parseLong(matcher.group(1));
                if (!ids.contains(id)) {
                    ids.add(id);
                }
            } catch (NumberFormatException ignored) {}
        }
        
        return ids;
    }

    private String normalizeUploadType(String uploadTypeRaw) {
        if (uploadTypeRaw == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String normalized = uploadTypeRaw.trim().toUpperCase();
        // DDL expects IMAGE or FILE or THUMBNAIL
        if ("INLINE_IMAGE".equals(normalized)) return TYPE_IMAGE;
        if ("ATTACHMENT".equals(normalized)) return TYPE_FILE;
        if (!TYPE_FILE.equals(normalized) && !TYPE_IMAGE.equals(normalized)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        return normalized;
    }

    private void validateFiles(List<MultipartFile> files, String uploadType) {
        if (files == null || files.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            long maxSize = TYPE_IMAGE.equals(uploadType) ? MAX_INLINE_IMAGE_SIZE : MAX_FILE_SIZE;
            if (file.getSize() > maxSize) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            if (TYPE_IMAGE.equals(uploadType)) {
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
                }
            }
        }
    }

    private String extractExtension(String originalName) {
        if (originalName != null) {
            int idx = originalName.lastIndexOf('.');
            if (idx >= 0 && idx < originalName.length() - 1) {
                return originalName.substring(idx);
            }
        }
        return "";
    }

    private boolean isImageContentType(String contentType) {
        return contentType != null && contentType.toLowerCase().startsWith("image/");
    }

    private String buildMarkdownSnippet(Long attachmentId, String originalName, String contentType, String downloadUrl, String imageUrl, long fileSize) {
        if (isImageContentType(contentType)) {
            return "![" + attachmentId + "](" + imageUrl + ")";
        }

        String safeName = originalName == null || originalName.isBlank() ? "download" : escapeMarkdownText(originalName);
        return "> [📎 " + safeName + "](" + downloadUrl + ")\n"
                + "> 다운로드 파일 · " + formatFileSize(fileSize);
    }

    private String escapeMarkdownText(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private String formatFileSize(long fileSize) {
        if (fileSize < 1024) {
            return fileSize + " B";
        }

        double value = fileSize;
        String[] units = {"KB", "MB", "GB", "TB"};
        int unitIndex = -1;

        while (value >= 1024 && unitIndex < units.length - 1) {
            value = value / 1024.0;
            unitIndex++;
        }

        return String.format(unitIndex == 0 ? "%.0f %s" : "%.1f %s", value, units[unitIndex]);
    }
}
