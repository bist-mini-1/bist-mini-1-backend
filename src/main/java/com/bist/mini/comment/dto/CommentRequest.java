package com.bist.mini.comment.dto;

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
public class CommentRequest {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long postId;

    private Long parentId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 500, message = "댓글 내용은 500자 이내로 입력해 주세요.")
    private String content;

    public com.bist.mini.comment.entity.Comment toEntity(Long memberId) {
        return com.bist.mini.comment.entity.Comment.builder()
                .postId(this.postId)
                .memberId(memberId)
                .parentId(this.parentId)
                .content(this.content)
                .isDeleted(com.bist.mini.common.enums.DeleteStatus.N)
                .build();
    }
}
