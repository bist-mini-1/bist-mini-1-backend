package com.bist.mini.comment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 댓글 좋아요 엔티티
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike {
    private Long likeId;
    private Long commentId;
    private Long memberId;
    private LocalDateTime createdAt;
}
