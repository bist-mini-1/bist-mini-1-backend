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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private static final String TYPE_IMAGE = "IMAGE";
    private static final String TYPE_FILE = "FILE";
    private static final long MAX_POST_TOTAL_SIZE = 200L * 1024 * 1024;
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_FILE_EXTENSIONS = Set.of("pdf", "hwp", "docx", "pptx", "xlsx", "zip");
    private static final Map<String, Set<String>> ALLOWED_MIME_TYPES = Map.of(
            "jpg", Set.of("image/jpeg"),
            "jpeg", Set.of("image/jpeg"),
            "png", Set.of("image/png"),
            "webp", Set.of("image/webp"),
            "pdf", Set.of("application/pdf"),
            "hwp", Set.of("application/x-hwp", "application/haansofthwp", "application/vnd.hancom.hwp"),
            "docx", Set.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
            "pptx", Set.of("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
            "xlsx", Set.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            "zip", Set.of("application/zip", "application/x-zip-compressed", "multipart/x-zip")
    );

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
        LinkedHashSet<Long> attachmentIdsForSizeValidation = new LinkedHashSet<>(attachmentIdsInContent);
        Long thumbnailId = extractIdFromUrl(thumbnailUrl);
        if (thumbnailId != null) {
            attachmentIdsForSizeValidation.add(thumbnailId);
        }

        validatePostAttachmentSize(new ArrayList<>(attachmentIdsForSizeValidation));
        
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
        if (thumbnailId != null) {
            // 해당 포스트의 기존 썸네일들을 모두 IMAGE로 초기화
            attachmentDao.resetThumbnailTypeByPostId(postId);
            // 선택된 이미지를 THUMBNAIL로 설정
            attachmentDao.updateType(thumbnailId, "THUMBNAIL");
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

            validateExtensionAndMimeType(file, uploadType);
        }
    }

    private String extractExtension(String originalName) {
        if (originalName == null || originalName.isBlank()) {
            return "";
        }

        int idx = originalName.lastIndexOf('.');
        if (idx >= 0 && idx < originalName.length() - 1) {
            return originalName.substring(idx + 1).trim().toLowerCase(Locale.ROOT);
        }
        return "";
    }

    private void validateExtensionAndMimeType(MultipartFile file, String uploadType) {
        String extension = extractExtension(file.getOriginalFilename());
        Set<String> allowedExtensions = TYPE_IMAGE.equals(uploadType) ? ALLOWED_IMAGE_EXTENSIONS : ALLOWED_FILE_EXTENSIONS;

        if (extension.isEmpty() || !allowedExtensions.contains(extension)) {
            throw new CustomException(ErrorCode.ATTACHMENT_EXTENSION_NOT_ALLOWED);
        }

        String contentType = normalizeContentType(file.getContentType());
        Set<String> allowedMimeTypes = ALLOWED_MIME_TYPES.get(extension);
        if (contentType.isEmpty() || allowedMimeTypes == null || !allowedMimeTypes.contains(contentType)) {
            throw new CustomException(ErrorCode.ATTACHMENT_MIME_TYPE_NOT_ALLOWED);
        }
    }

    private void validatePostAttachmentSize(List<Long> attachmentIds) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return;
        }

        List<Attachment> attachments = attachmentDao.findByIds(attachmentIds);
        if (attachments.size() != attachmentIds.size()) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }

        long totalSize = attachments.stream()
                .mapToLong(attachment -> attachment.getFile_size() == null ? 0L : attachment.getFile_size())
                .sum();

        if (totalSize > MAX_POST_TOTAL_SIZE) {
            throw new CustomException(ErrorCode.ATTACHMENT_TOTAL_SIZE_EXCEEDED);
        }
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null) {
            return "";
        }
        String normalized = contentType.trim().toLowerCase(Locale.ROOT);
        int separatorIndex = normalized.indexOf(';');
        if (separatorIndex >= 0) {
            normalized = normalized.substring(0, separatorIndex).trim();
        }
        return normalized;
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
