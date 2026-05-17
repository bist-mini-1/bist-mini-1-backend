package com.bist.mini.post.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostPageResponse {

    private List<PostListResponse> posts;
    private int page;
    private int size;
    private long totalCount;
    private int totalPages;
}