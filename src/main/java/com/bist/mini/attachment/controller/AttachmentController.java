package com.bist.mini.attachment.controller;

import com.bist.mini.attachment.dto.AttachmentUploadResponse;
import com.bist.mini.attachment.entity.Attachment;
import com.bist.mini.attachment.service.AttachmentService;
import com.bist.mini.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "Attachment", description = "첨부파일 관리 API")
@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Operation(summary = "파일 업로드", description = "게시글 첨부/본문 이미지 파일을 DB에 저장하고 attachment ID를 발급받습니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<AttachmentUploadResponse>> uploadAttachments(
            @Parameter(description = "업로드 타입 (IMAGE 또는 FILE)", example = "IMAGE")
            @RequestParam String uploadType,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return ApiResponse.success(attachmentService.uploadFiles(files, uploadType));
    }

    @Operation(summary = "파일 조회", description = "DB에 저장된 파일을 조회합니다.")
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

    @Operation(summary = "파일 다운로드", description = "DB에 저장된 파일을 다운로드합니다.")
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        Attachment attachment = attachmentService.getAttachment(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFile_type() != null ? attachment.getFile_type() : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(attachment.getOriginal_name(), StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .body(attachment.getFile_data());
    }
}
