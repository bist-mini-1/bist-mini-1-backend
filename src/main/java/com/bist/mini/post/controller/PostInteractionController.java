package com.bist.mini.post.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.annotation.LoginMember;
import com.bist.mini.post.service.BookmarkService;
import com.bist.mini.post.service.LikeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostInteractionController {

    private final LikeService likeService;
    private final BookmarkService bookmarkService;

    @PostMapping("/{postId}/like")
    public ApiResponse<Boolean> toggleLike(
            @PathVariable("postId") Long postId,
            @LoginMember Long memberId) {
        log.debug("게시글 좋아요 토글 요청: postId={}, memberId={}", postId, memberId);
        return ApiResponse.success(likeService.toggleLike(postId, memberId));
    }

    @PostMapping("/{postId}/bookmark")
    public ApiResponse<Boolean> toggleBookmark(
            @PathVariable("postId") Long postId,
            @LoginMember Long memberId) {
        log.debug("게시글 북마크 토글 요청: postId={}, memberId={}", postId, memberId);
        return ApiResponse.success(bookmarkService.toggleBookmark(postId, memberId));
    }
}
