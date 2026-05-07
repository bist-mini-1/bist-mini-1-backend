package com.bist.mini.mypage.dto;

import com.bist.mini.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * GET /api/members/me/posts 응답 DTO
 * - Post 엔티티 내부 필드(isDeleted, isTemp 등)를 외부에 노출하지 않기 위한 래퍼
 */
@Getter
@Builder
public class MyPostResponse {

    private Long postId;
    private String title;
    private String content;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MyPostResponse from(Post post) {
        return MyPostResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isPublic(post.getIsPublic())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
