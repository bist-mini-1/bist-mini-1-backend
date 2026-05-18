package com.bist.mini.post.dto;

import com.bist.mini.post.entity.Post;
import com.bist.mini.post.entity.Tag;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostResponse {

    private Long postId;

    private Long memberId;

    private String nickname;

    private String title;

    private String content;

    private Long viewCount;

    private Long likeCount;

    private Long commentCount;

    private String isPublic;

    private String isTemp;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<Tag> tags;

    private Boolean isLiked;

    private Boolean isBookmarked;

    public static PostResponse of(Post post, String nickname, List<Tag> tags, Boolean isLiked, Boolean isBookmarked) {
        return PostResponse.builder()
                .postId(post.getPostId())
                .memberId(post.getMemberId())
                .nickname(nickname)
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isPublic(post.getIsPublic())
                .isTemp(post.getIsTemp())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .tags(tags)
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .build();
    }
}
