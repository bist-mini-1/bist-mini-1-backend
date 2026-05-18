package com.bist.mini.mypage.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.annotation.LoginMember;
import com.bist.mini.common.jwt.JwtProvider;
import com.bist.mini.mypage.dto.BioUpdateRequest;
import com.bist.mini.mypage.dto.InterestTagUpdateRequest;
import com.bist.mini.mypage.dto.MemberProfileResponse;
import com.bist.mini.mypage.dto.NicknameUpdateRequest;
import com.bist.mini.mypage.dto.PasswordUpdateRequest;
import com.bist.mini.mypage.dto.MyPostResponse;
import com.bist.mini.mypage.dto.ProfileImageUpdateResponse;
import com.bist.mini.mypage.service.MyPageService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 마이페이지 API 컨트롤러
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final JwtProvider jwtProvider;

    // ── 헬퍼: JWT에서 memberId 추출 ───────────────────────────────────────────

    private Long extractMemberId(HttpServletRequest httpRequest) {
        String authorization = httpRequest.getHeader("Authorization");
        return jwtProvider.getMemberIdFromToken(authorization);
    }

    /** 요청의 서버 기본 URL (예: http://localhost:8080) */
    private String extractBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    // ── 프로필 조회 ──────────────────────────────────────────────────────────

    @GetMapping("/me")
    public ApiResponse<MemberProfileResponse> getMyProfile(HttpServletRequest httpRequest) {
        Long memberId = extractMemberId(httpRequest);
        String baseUrl = extractBaseUrl(httpRequest);
        return ApiResponse.success(myPageService.getProfile(memberId, baseUrl));
    }

    // ── 내 게시글 목록 ─────────────────────────────────────────────────────────

    @GetMapping("/me/posts")
    public ApiResponse<List<MyPostResponse>> getMyPosts(HttpServletRequest httpRequest) {
        Long memberId = extractMemberId(httpRequest);
        return ApiResponse.success(myPageService.getMyPosts(memberId));
    }

    // ── 타인 프로필 조회 ─────────────────────────────────────────────────────────

    @GetMapping("/{memberId}/profile")
    public ApiResponse<MemberProfileResponse> getUserProfile(
            @PathVariable Long memberId,
            HttpServletRequest httpRequest
    ) {
        String baseUrl = extractBaseUrl(httpRequest);
        return ApiResponse.success(myPageService.getUserProfile(memberId, baseUrl));
    }

    // ── 타인 공개 게시글 목록 ───────────────────────────────────────────────────

    @GetMapping("/{memberId}/posts")
    public ApiResponse<List<MyPostResponse>> getUserPosts(
            @PathVariable Long memberId
    ) {
        return ApiResponse.success(myPageService.getUserPosts(memberId));
    }

    // ── 북마크한 게시글 목록 ────────────────────────────────────────────────────

    @GetMapping("/me/bookmarks")
    public ApiResponse<List<MyPostResponse>> getBookmarkedPosts(HttpServletRequest httpRequest) {
        Long memberId = extractMemberId(httpRequest);
        return ApiResponse.success(myPageService.getBookmarkedPosts(memberId));
    }

    // ── 닉네임 수정 ──────────────────────────────────────────────────────────

    @PatchMapping("/me/nickname")
    public ApiResponse<Void> updateNickname(
            HttpServletRequest httpRequest,
            @Valid @RequestBody NicknameUpdateRequest request
    ) {
        Long memberId = extractMemberId(httpRequest);
        myPageService.updateNickname(memberId, request);
        return ApiResponse.success();
    }

    // ── 비밀번호 변경 ─────────────────────────────────────────────────────────

    @PatchMapping("/me/password")
    public ApiResponse<Void> updatePassword(
            HttpServletRequest httpRequest,
            @Valid @RequestBody PasswordUpdateRequest request
    ) {
        Long memberId = extractMemberId(httpRequest);
        myPageService.updatePassword(memberId, request);
        return ApiResponse.success();
    }

    // ── 자기소개 수정 ─────────────────────────────────────────────────────────

    @PatchMapping("/me/bio")
    public ApiResponse<Void> updateBio(
            HttpServletRequest httpRequest,
            @Valid @RequestBody BioUpdateRequest request
    ) {
        Long memberId = extractMemberId(httpRequest);
        myPageService.updateBio(memberId, request);
        return ApiResponse.success();
    }

    // ── 프로필 이미지 업로드 ───────────────────────────────────────────────────

    @PatchMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProfileImageUpdateResponse> updateProfileImage(
            HttpServletRequest httpRequest,
            @RequestPart("profileImage") MultipartFile profileImage
    ) {
        Long memberId = extractMemberId(httpRequest);
        String baseUrl = extractBaseUrl(httpRequest);
        return ApiResponse.success(myPageService.updateProfileImage(memberId, profileImage, baseUrl));
    }

    // ── 프로필 이미지 조회 ────────────────────────────────────────────────────

    @GetMapping("/me/profile-image")
    public ResponseEntity<byte[]> getMyProfileImage(
            @LoginMember Long memberId
    ) {
        return myPageService.getProfileImage(memberId);
    }

    @GetMapping("/{memberId}/profile-image")
    public ResponseEntity<byte[]> getProfileImage(
            @PathVariable Long memberId
    ) {
        return myPageService.getProfileImage(memberId);
    }

    // ── 관심 태그 조회 ────────────────────────────────────────────────────────

    @GetMapping("/me/interest-tags")
    public ApiResponse<List<Long>> getInterestTags(HttpServletRequest httpRequest) {
        Long memberId = extractMemberId(httpRequest);
        return ApiResponse.success(myPageService.getInterestTags(memberId));
    }

    // ── 관심 태그 수정 ────────────────────────────────────────────────────────

    @PatchMapping("/me/interest-tags")
    public ApiResponse<Void> updateInterestTags(
            HttpServletRequest httpRequest,
            @RequestBody InterestTagUpdateRequest request
    ) {
        Long memberId = extractMemberId(httpRequest);
        myPageService.updateInterestTags(memberId, request);
        return ApiResponse.success();
    }
}
