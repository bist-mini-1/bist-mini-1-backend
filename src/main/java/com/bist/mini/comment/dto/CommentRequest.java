package com.bist.mini.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 댓글 요청 DTO
 */
@Getter
@Setter
@Schema(description = "댓글 요청 데이터")
public class CommentRequest {

    @NotNull(message = "게시글 ID는 필수입니다.")
    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "8")
    private Long parentId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 2000, message = "댓글 내용은 2000자 이내로 입력해 주세요.")
    @Schema(description = "댓글 내용", example = "정말 유익한 포스팅이네요!")
    private String content;

}
