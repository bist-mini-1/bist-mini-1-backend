package com.bist.mini.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "첨부 업로드 응답")
public class AttachmentUploadResponse {

    @Schema(description = "첨부 ID", example = "101")
    private Long attachmentId;

    @Schema(description = "원본 파일명", example = "sample.png")
    private String originalName;

    @Schema(description = "접근 URL", example = "/uploads/post/9f8f1d4f-128f-4b4f-aec8-b31ea98c024e.png")
    private String fileUrl;

    @Schema(description = "업로드 타입", example = "INLINE_IMAGE")
    private String uploadType;

    @Schema(description = "파일 크기(byte)", example = "12034")
    private Long fileSize;
}