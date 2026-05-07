package com.bist.mini.attachment.controller;

import com.bist.mini.attachment.dto.AttachmentUploadResponse;
import com.bist.mini.attachment.service.AttachmentService;
import com.bist.mini.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Attachment", description = "첨부파일 관리 API")
@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Operation(summary = "첨부파일 업로드", description = "게시글 첨부/본문 이미지 파일을 먼저 업로드하고 attachmentId를 발급받습니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<AttachmentUploadResponse>> uploadAttachments(
            @Parameter(description = "업로드 타입 (ATTACHMENT 또는 INLINE_IMAGE)", example = "INLINE_IMAGE")
            @RequestParam String uploadType,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return ApiResponse.success(attachmentService.uploadFiles(files, uploadType));
    }
}
