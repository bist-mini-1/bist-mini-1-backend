package com.bist.mini.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 댓글 수정 요청 DTO
 */
@Getter
@Setter
@Schema(description = "댓글 수정 요청 데이터")
public class CommentUpdateRequest {

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 500, message = "댓글 내용은 500자 이내로 입력해 주세요.")
    @Schema(description = "수정할 댓글 내용", example = "내용을 수정했습니다.")
    private String content;

}
