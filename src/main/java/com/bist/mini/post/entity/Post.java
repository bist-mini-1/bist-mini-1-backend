package com.bist.mini.post.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private Long postId;
    private Long memberId;
    private String title;
    private String content;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String isPublic;
    private String isTemp;
    private String isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private List<String> tags;

}
