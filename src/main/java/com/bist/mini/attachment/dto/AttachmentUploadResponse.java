package com.bist.mini.attachment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "첨부 업로드 응답")
public class AttachmentUploadResponse {

    @Schema(description = "첨부파일 ID (DB)", example = "1627")
    private Long attachmentId;

    @Schema(description = "원본 파일명", example = "sample.png")
    private String originalName;

    @Schema(description = "접근 URL", example = "/api/attachments/1627/image")
    private String fileUrl;

    @Schema(description = "업로드 타입", example = "IMAGE")
    private String uploadType;

    @Schema(description = "파일 크기(byte)", example = "12034")
    private Long fileSize;
}
