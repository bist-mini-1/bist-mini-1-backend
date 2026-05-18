package com.bist.mini.follow.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.jwt.JwtProvider;
import com.bist.mini.follow.dto.FollowCountResponse;
import com.bist.mini.follow.dto.FollowListResponse;
import com.bist.mini.follow.service.FollowService;
import com.bist.mini.mypage.dto.MyPostResponse;
import java.util.List;
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

    private String extractBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    // ── 팔로우 ─────────────────────────────────────────────────────────────────

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

    @GetMapping("/{memberId}/count")
    public ApiResponse<FollowCountResponse> getFollowCount(
            @PathVariable Long memberId
    ) {
        return ApiResponse.success(followService.getFollowCount(memberId));
    }

    // ── 팔로워 목록 조회 ──────────────────────────────────────────────────────────

    @GetMapping("/{memberId}/followers")
    public ApiResponse<FollowListResponse> getFollowers(
            @PathVariable Long memberId,
            HttpServletRequest httpRequest
    ) {
        String baseUrl = extractBaseUrl(httpRequest);
        return ApiResponse.success(followService.getFollowers(memberId, baseUrl));
    }

    // ── 팔로잉 목록 조회 ──────────────────────────────────────────────────────────

    @GetMapping("/{memberId}/followings")
    public ApiResponse<FollowListResponse> getFollowings(
            @PathVariable Long memberId,
            HttpServletRequest httpRequest
    ) {
        String baseUrl = extractBaseUrl(httpRequest);
        return ApiResponse.success(followService.getFollowings(memberId, baseUrl));
    }

    @GetMapping("/me/followers")
    public ApiResponse<FollowListResponse> getMyFollowers(HttpServletRequest httpRequest) {
        Long memberId = extractMemberId(httpRequest);
        String baseUrl = extractBaseUrl(httpRequest);
        return ApiResponse.success(followService.getFollowers(memberId, baseUrl));
    }

    @GetMapping("/me/followings")
    public ApiResponse<FollowListResponse> getMyFollowings(HttpServletRequest httpRequest) {
        Long memberId = extractMemberId(httpRequest);
        String baseUrl = extractBaseUrl(httpRequest);
        return ApiResponse.success(followService.getFollowings(memberId, baseUrl));
    }

    // ── 팔로우한 사용자 게시글 조회 ──────────────────────────────────────────────

    @GetMapping("/feed")
    public ApiResponse<List<MyPostResponse>> getFollowingPosts(
            HttpServletRequest httpRequest
    ) {
        Long memberId = extractMemberId(httpRequest);
        return ApiResponse.success(followService.getFollowingPosts(memberId));
    }
}
