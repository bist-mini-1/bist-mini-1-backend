package com.bist.mini.attachment.service;

import com.bist.mini.attachment.dao.AttachmentDao;
import com.bist.mini.attachment.dto.AttachmentUploadResponse;
import com.bist.mini.attachment.entity.Attachment;
import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private static final String TYPE_IMAGE = "IMAGE";
    private static final String TYPE_FILE = "FILE";
    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;
    private static final long MAX_INLINE_IMAGE_SIZE = 10L * 1024 * 1024;

    private final AttachmentDao attachmentDao;

    private final Path tempUploadRoot = Paths.get("uploads", "temp").toAbsolutePath().normalize();
    
    // In-memory cache for temp file metadata
    private final ConcurrentHashMap<String, TempFileMeta> tempFileCache = new ConcurrentHashMap<>();

    private static class TempFileMeta {
        String originalName;
        String contentType;
        long size;
        String uploadType;
        Path filePath;
        
        TempFileMeta(String originalName, String contentType, long size, String uploadType, Path filePath) {
            this.originalName = originalName;
            this.contentType = contentType;
            this.size = size;
            this.uploadType = uploadType;
            this.filePath = filePath;
        }
    }

    @PostConstruct
    public void initUploadDir() {
        try {
            Files.createDirectories(tempUploadRoot);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<AttachmentUploadResponse> uploadTempFiles(List<MultipartFile> files, String uploadTypeRaw) {
        String uploadType = normalizeUploadType(uploadTypeRaw);
        validateFiles(files, uploadType);

        List<AttachmentUploadResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            String tempId = UUID.randomUUID().toString();
            String extension = extractExtension(file.getOriginalFilename());
            String storedName = tempId + extension;
            Path target = tempUploadRoot.resolve(storedName);
            try {
                file.transferTo(target);
            } catch (IOException e) {
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            tempFileCache.put(tempId, new TempFileMeta(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                uploadType,
                target
            ));

            String fileUrl = "/api/attachments/temp/" + tempId;
            responses.add(AttachmentUploadResponse.builder()
                    .tempId(tempId)
                    .originalName(file.getOriginalFilename())
                    .fileUrl(fileUrl)
                    .uploadType(uploadType)
                    .fileSize(file.getSize())
                    .build());
        }
        return responses;
    }

    public Resource loadTempFileAsResource(String tempId) {
        TempFileMeta meta = tempFileCache.get(tempId);
        if (meta == null) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }
        try {
            Resource resource = new UrlResource(meta.filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
            }
        } catch (Exception ex) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String getMimeTypeForTempFile(String tempId) {
        TempFileMeta meta = tempFileCache.get(tempId);
        return meta != null ? meta.contentType : null;
    }

    public Attachment getAttachment(Long id) {
        List<Attachment> list = attachmentDao.findByIds(List.of(id));
        if (list.isEmpty()) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }
        return list.get(0);
    }

    @Transactional
    public String syncPostAttachments(Long postId, List<String> tempAttachmentIdsRaw, List<String> tempInlineImageIdsRaw, String content) {
        List<String> tempAttachmentIds = normalizeIds(tempAttachmentIdsRaw);
        List<String> tempInlineImageIds = normalizeIds(tempInlineImageIdsRaw);

        String updatedContent = content;

        // Process Inline Images
        for (String tempId : tempInlineImageIds) {
            TempFileMeta meta = tempFileCache.get(tempId);
            if (meta == null) continue;

            Attachment attachment = saveTempFileToDb(postId, meta);
            String tempUrl = "/api/attachments/temp/" + tempId;
            String permanentUrl = "/api/attachments/" + attachment.getAttachment_id() + "/download";
            
            if (updatedContent != null) {
                updatedContent = updatedContent.replace(tempUrl, permanentUrl);
            }
            
            cleanUpTempFile(tempId, meta.filePath);
        }

        // Process standard attachments
        for (String tempId : tempAttachmentIds) {
            TempFileMeta meta = tempFileCache.get(tempId);
            if (meta == null) continue;

            saveTempFileToDb(postId, meta);
            cleanUpTempFile(tempId, meta.filePath);
        }

        // Handle deletions of previously saved attachments if content has changed and images were removed
        // For simplicity in this new model, we just ensure existing attachments not present in content are soft-deleted.
        // If it's an update, the frontend should explicitly send IDs to delete if needed, but since we are replacing content, 
        // a more complex sync might be needed. Let's keep it simple for now or assume inline images not in content are deleted.
        if (updatedContent != null) {
            List<Attachment> currentAttachments = attachmentDao.findActiveByPostId(postId);
            List<Long> toRemove = new ArrayList<>();
            for (Attachment att : currentAttachments) {
                if (TYPE_IMAGE.equals(att.getUpload_type())) {
                    String permUrl = "/api/attachments/" + att.getAttachment_id() + "/download";
                    if (!updatedContent.contains(permUrl)) {
                        toRemove.add(att.getAttachment_id());
                    }
                }
            }
            if (!toRemove.isEmpty()) {
                attachmentDao.softDeleteByIds(toRemove);
            }
        }

        return updatedContent;
    }

    private Attachment saveTempFileToDb(Long postId, TempFileMeta meta) {
        try {
            byte[] fileData = Files.readAllBytes(meta.filePath);
            String extension = extractExtension(meta.originalName);
            if (extension.startsWith(".")) {
                extension = extension.substring(1);
            }

            Attachment attachment = Attachment.builder()
                    .post_id(postId)
                    .original_name(meta.originalName)
                    .file_size(meta.size)
                    .file_type(meta.contentType)
                    .extension(extension.isEmpty() ? null : extension)
                    .upload_type(meta.uploadType)
                    .file_data(fileData)
                    .download_count(0L)
                    .is_deleted("N")
                    .created_at(LocalDateTime.now())
                    .build();

            attachmentDao.insert(attachment);
            return attachment;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void cleanUpTempFile(String tempId, Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {}
        tempFileCache.remove(tempId);
    }

    private List<String> normalizeIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream()
                .filter(id -> id != null && !id.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
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
}
