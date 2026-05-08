package com.bist.mini.follow.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.jwt.JwtProvider;
import com.bist.mini.follow.dto.FollowCountResponse;
import com.bist.mini.follow.service.FollowService;
import com.bist.mini.mypage.dto.MyPostResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 팔로우 API 컨트롤러
 */
@Tag(name = "Follow", description = "팔로우 API")
@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final JwtProvider jwtProvider;

    // ── 헬퍼: JWT에서 memberId 추출 ───────────────────────────────────────────

    private Long extractMemberId(HttpServletRequest httpRequest) {
        String authorization = httpRequest.getHeader("Authorization");
        return jwtProvider.getMemberIdFromToken(authorization);
    }

    // ── 팔로우 ─────────────────────────────────────────────────────────────────

    @Operation(summary = "사용자 팔로우", description = "다른 사용자를 팔로우합니다.")
    @PostMapping("/{followingId}")
    public ApiResponse<Void> follow(
            HttpServletRequest httpRequest,
            @PathVariable Long followingId
    ) {
        Long followerId = extractMemberId(httpRequest);
        followService.follow(followerId, followingId);
        return ApiResponse.success();
    }

    // ── 팔로우 취소 ────────────────────────────────────────────────────────────

    @Operation(summary = "팔로우 취소", description = "팔로우한 사용자를 팔로우 취소합니다.")
    @DeleteMapping("/{followingId}")
    public ApiResponse<Void> unfollow(
            HttpServletRequest httpRequest,
            @PathVariable Long followingId
    ) {
        Long followerId = extractMemberId(httpRequest);
        followService.unfollow(followerId, followingId);
        return ApiResponse.success();
    }

    // ── 팔로워/팔로잉 수 조회 ──────────────────────────────────────────────────

    @Operation(summary = "팔로워/팔로잉 수 조회", description = "특정 사용자의 팔로워 수와 팔로잉 수를 조회합니다.")
    @GetMapping("/{memberId}/count")
    public ApiResponse<FollowCountResponse> getFollowCount(
            @PathVariable Long memberId
    ) {
        return ApiResponse.success(followService.getFollowCount(memberId));
    }

    // ── 팔로워 수 조회 ────────────────────────────────────────────────────────

    @Operation(summary = "팔로워 수 조회", description = "특정 사용자를 팔로우하는 사람 수를 조회합니다.")
    @GetMapping("/{memberId}/followers/count")
    public ApiResponse<Long> getFollowerCount(
            @PathVariable Long memberId
    ) {
        return ApiResponse.success(followService.getFollowerCount(memberId));
    }

    // ── 팔로잉 수 조회 ────────────────────────────────────────────────────────

    @Operation(summary = "팔로잉 수 조회", description = "특정 사용자가 팔로우하는 사람 수를 조회합니다.")
    @GetMapping("/{memberId}/followings/count")
    public ApiResponse<Long> getFollowingCount(
            @PathVariable Long memberId
    ) {
        return ApiResponse.success(followService.getFollowingCount(memberId));
    }

    // ── 팔로우한 사용자 게시글 조회 ──────────────────────────────────────────────

    @Operation(summary = "팔로우한 사용자 게시글 조회", description = "내가 팔로우한 사용자들의 게시글 목록을 최신순으로 조회합니다.")
    @GetMapping("/feed")
    public ApiResponse<List<MyPostResponse>> getFollowingPosts(
            HttpServletRequest httpRequest
    ) {
        Long memberId = extractMemberId(httpRequest);
        return ApiResponse.success(followService.getFollowingPosts(memberId));
    }
}
