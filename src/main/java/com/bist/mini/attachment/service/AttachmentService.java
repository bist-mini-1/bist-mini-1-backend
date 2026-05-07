package com.bist.mini.attachment.service;

import com.bist.mini.attachment.dao.AttachmentDao;
import com.bist.mini.attachment.dto.AttachmentUploadResponse;
import com.bist.mini.attachment.entity.Attachment;
import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private static final String TYPE_ATTACHMENT = "ATTACHMENT";
    private static final String TYPE_INLINE_IMAGE = "INLINE_IMAGE";
    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;
    private static final long MAX_INLINE_IMAGE_SIZE = 10L * 1024 * 1024;

    private final AttachmentDao attachmentDao;

    private final Path uploadRoot = Paths.get("uploads", "post").toAbsolutePath().normalize();

    @PostConstruct
    public void initUploadDir() {
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public List<AttachmentUploadResponse> uploadFiles(List<MultipartFile> files, String uploadTypeRaw) {
        String uploadType = normalizeUploadType(uploadTypeRaw);
        validateFiles(files, uploadType);

        List<AttachmentUploadResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            String storedName = buildStoredFileName(file.getOriginalFilename());
            Path target = uploadRoot.resolve(storedName);
            try {
                file.transferTo(target);
            } catch (IOException e) {
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            String fileUrl = "/uploads/post/" + storedName;
            Attachment attachment = Attachment.builder()
                    .post_id(null)
                    .original_name(file.getOriginalFilename())
                    .file_size(file.getSize())
                    .file_type(file.getContentType())
                    .upload_type(uploadType)
                    .file_data(fileUrl)
                    .download_count(0L)
                    .is_deleted("N")
                    .created_at(LocalDateTime.now())
                    .build();

            attachmentDao.insert(attachment);
            responses.add(AttachmentUploadResponse.builder()
                    .attachmentId(attachment.getAttachment_id())
                    .originalName(attachment.getOriginal_name())
                    .fileUrl(attachment.getFile_data())
                    .uploadType(attachment.getUpload_type())
                    .fileSize(attachment.getFile_size())
                    .build());
        }
        return responses;
    }

    @Transactional
    public void syncPostAttachments(Long postId, List<Long> attachmentIdsRaw, List<Long> inlineImageIdsRaw, String content) {
        List<Long> attachmentIds = normalizeIds(attachmentIdsRaw);
        List<Long> inlineImageIds = normalizeIds(inlineImageIdsRaw);

        validateInlineImagesInContent(content, inlineImageIds);

        Set<Long> desiredIds = new LinkedHashSet<>();
        desiredIds.addAll(attachmentIds);
        desiredIds.addAll(inlineImageIds);

        if (!desiredIds.isEmpty()) {
            attachmentDao.bindToPost(postId, new ArrayList<>(desiredIds));
        }

        List<Attachment> current = attachmentDao.findActiveByPostId(postId);
        List<Long> removeIds = current.stream()
                .map(Attachment::getAttachment_id)
                .filter(id -> !desiredIds.contains(id))
                .toList();

        if (!removeIds.isEmpty()) {
            attachmentDao.softDeleteByIds(removeIds);
        }
    }

    private void validateInlineImagesInContent(String content, List<Long> inlineImageIds) {
        if (inlineImageIds.isEmpty()) {
            return;
        }

        if (content == null || content.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        List<Attachment> attachments = attachmentDao.findByIds(inlineImageIds);
        if (attachments.size() != inlineImageIds.size()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        for (Attachment attachment : attachments) {
            if (!TYPE_INLINE_IMAGE.equalsIgnoreCase(attachment.getUpload_type())) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
            if (!content.contains(attachment.getFile_data())) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
        }
    }

    private List<Long> normalizeIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return ids.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .collect(Collectors.toList());
    }

    private String normalizeUploadType(String uploadTypeRaw) {
        if (uploadTypeRaw == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String normalized = uploadTypeRaw.trim().toUpperCase();
        if (!TYPE_ATTACHMENT.equals(normalized) && !TYPE_INLINE_IMAGE.equals(normalized)) {
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

            long maxSize = TYPE_INLINE_IMAGE.equals(uploadType) ? MAX_INLINE_IMAGE_SIZE : MAX_FILE_SIZE;
            if (file.getSize() > maxSize) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            if (TYPE_INLINE_IMAGE.equals(uploadType)) {
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
                }
            }
        }
    }

    private String buildStoredFileName(String originalName) {
        String extension = "";
        if (originalName != null) {
            int idx = originalName.lastIndexOf('.');
            if (idx >= 0 && idx < originalName.length() - 1) {
                extension = "." + originalName.substring(idx + 1);
            }
        }
        return UUID.randomUUID() + extension;
    }
}
