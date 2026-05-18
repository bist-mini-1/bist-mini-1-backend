package com.bist.mini.attachment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttachmentUploadResponse {

    private Long attachmentId;

    private String originalName;

    private String fileUrl;

    private String downloadUrl;

    private Boolean image;

    private String markdown;

    private String uploadType;

    private Long fileSize;
}
