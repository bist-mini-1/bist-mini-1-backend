package com.bist.mini.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostListResponse {

    private Long postId;
    private Long memberId;
    private String nickname;
    private String title;
    private String contentPreview;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime createdAt;

    private String thumbnailUrl;

    private List<String> tags;
}