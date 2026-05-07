package com.bist.mini.attachment.controller;

import com.bist.mini.attachment.dto.AttachmentUploadResponse;
import com.bist.mini.attachment.entity.Attachment;
import com.bist.mini.attachment.service.AttachmentService;
import com.bist.mini.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Attachment", description = "첨부파일 관리 API")
@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Operation(summary = "첨부파일 임시 업로드", description = "게시글 첨부/본문 이미지 파일을 임시 폴더에 업로드하고 임시 ID(UUID)를 발급받습니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<AttachmentUploadResponse>> uploadAttachments(
            @Parameter(description = "업로드 타입 (IMAGE 또는 FILE)", example = "IMAGE")
            @RequestParam String uploadType,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return ApiResponse.success(attachmentService.uploadTempFiles(files, uploadType));
    }

    @Operation(summary = "임시 파일 조회", description = "임시 폴더에 저장된 이미지를 조회합니다.")
    @GetMapping("/temp/{tempId}")
    public ResponseEntity<Resource> getTempFile(@PathVariable String tempId) {
        Resource resource = attachmentService.loadTempFileAsResource(tempId);
        String contentType = attachmentService.getMimeTypeForTempFile(tempId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .body(resource);
    }

    @Operation(summary = "첨부파일 다운로드/조회", description = "DB에 저장된 첨부파일 바이너리를 다운로드/조회합니다.")
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        Attachment attachment = attachmentService.getAttachment(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFile_type() != null ? attachment.getFile_type() : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getOriginal_name() + "\"")
                .body(attachment.getFile_data());
    }

    @GetMapping("/{attachmentId}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long attachmentId) {
        Attachment attachment = attachmentService.getAttachment(attachmentId);

        return ResponseEntity.ok()
                .header("Content-Type", attachment.getFile_type())
                .body(attachment.getFile_data());
    }
}
