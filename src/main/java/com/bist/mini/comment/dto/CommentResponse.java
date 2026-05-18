package com.bist.mini.comment.dto;

import com.bist.mini.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long commentId;

    private Long postId;

    private Long memberId;

    private Long parentId;

    private String content;

    private String isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private String nickname;

    private String profileImageUrl;

    private int likeCount;

    private boolean isMine;

    private boolean canDelete;

    private boolean isLiked;

    public static CommentResponse from(Comment comment, String nickname, String profileImageUrl, int likeCount,
            boolean isLiked, boolean isMine, boolean canDelete) {
        if (comment == null)
            return null;
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPostId())
                .memberId(comment.getMemberId())
                .parentId(comment.getParentId())
                .content(comment.getContent())
                .isDeleted(comment.getIsDeleted() != null ? comment.getIsDeleted().name() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .deletedAt(comment.getDeletedAt())
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .likeCount(likeCount)
                .isLiked(isLiked)
                .isMine(isMine)
                .canDelete(canDelete)
                .build();
    }

}
