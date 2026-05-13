package com.bist.mini.mypage.dto;

import com.bist.mini.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * GET /api/members/me/posts 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPostResponse {

    private Long postId;
    private Long memberId;
    private String nickname;
    private String title;
    private String content;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String isPublic;
    private Boolean isLiked;
    private Boolean isBookmarked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String thumbnailUrl;
    private List<String> tags;

    public static MyPostResponse from(Post post) {
        if (post == null) return null;
        return MyPostResponse.builder()
                .postId(post.getPostId())
                .memberId(post.getMemberId())
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
