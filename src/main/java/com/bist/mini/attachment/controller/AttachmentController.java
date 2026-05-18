package com.bist.mini.attachment.controller;

import com.bist.mini.attachment.dto.AttachmentUploadResponse;
import com.bist.mini.attachment.entity.Attachment;
import com.bist.mini.attachment.service.AttachmentService;
import com.bist.mini.common.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<AttachmentUploadResponse>> uploadAttachments(
            @RequestParam String uploadType,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return ApiResponse.success(attachmentService.uploadFiles(files, uploadType));
    }

    @GetMapping("/{attachmentId}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long attachmentId) {
        Attachment attachment = attachmentService.getAttachment(attachmentId);
        
        String contentType = attachment.getFile_type();
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(attachment.getFile_data().length)
                .header(HttpHeaders.CACHE_CONTROL, "max-age=604800") // 1주일 캐싱
                .body(attachment.getFile_data());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        Attachment attachment = attachmentService.getAttachment(id);
        String fileType = attachment.getFile_type();
        if (fileType == null || fileType.isEmpty()) {
            fileType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(attachment.getOriginal_name(), StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .body(attachment.getFile_data());
    }
}
