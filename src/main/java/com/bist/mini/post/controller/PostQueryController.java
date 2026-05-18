package com.bist.mini.post.controller;

import com.bist.mini.common.annotation.LoginMember;
import com.bist.mini.post.dto.PostListResponse;
import com.bist.mini.post.dto.PostPageResponse;
import com.bist.mini.post.service.PostQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시글 조회 전용 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostQueryController {

    private final PostQueryService postQueryService;

    @GetMapping
    public PostPageResponse getPostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sort,
            @LoginMember(required = false) Long memberId
    ) {
        return postQueryService.getPostList(page, size, keyword, sort, memberId);
    }

    @GetMapping("/{postId}/recommended")
    public List<PostListResponse> getRecommendedPosts(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "4") int limit,
            @LoginMember(required = false) Long memberId
    ) {
        return postQueryService.getRecommendedPosts(postId, limit, memberId);
    }
}
