package com.bist.mini.post.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.annotation.LoginMember;
import com.bist.mini.post.service.BookmarkService;
import com.bist.mini.post.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Post Interaction", description = "게시글 상호작용(좋아요, 북마크) API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostInteractionController {

    private final LikeService likeService;
    private final BookmarkService bookmarkService;

    @Operation(summary = "좋아요 토글", description = "게시글에 좋아요를 누르거나 취소합니다.")
    @PostMapping("/{postId}/like")
    public ApiResponse<Boolean> toggleLike(
            @Parameter(description = "게시글 ID") @PathVariable("postId") Long postId,
            @LoginMember Long memberId) {
        log.debug("게시글 좋아요 토글 요청: postId={}, memberId={}", postId, memberId);
        return ApiResponse.success(likeService.toggleLike(postId, memberId));
    }

    @Operation(summary = "북마크 토글", description = "게시글을 북마크하거나 취소합니다.")
    @PostMapping("/{postId}/bookmark")
    public ApiResponse<Boolean> toggleBookmark(
            @Parameter(description = "게시글 ID") @PathVariable("postId") Long postId,
            @LoginMember Long memberId) {
        log.debug("게시글 북마크 토글 요청: postId={}, memberId={}", postId, memberId);
        return ApiResponse.success(bookmarkService.toggleBookmark(postId, memberId));
    }
}
