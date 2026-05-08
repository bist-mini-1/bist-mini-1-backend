package com.bist.mini.mypage.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.jwt.JwtProvider;
import com.bist.mini.mypage.dto.BioUpdateRequest;
import com.bist.mini.mypage.dto.MemberProfileResponse;
import com.bist.mini.mypage.dto.NicknameUpdateRequest;
import com.bist.mini.mypage.dto.PasswordUpdateRequest;
import com.bist.mini.mypage.dto.MyPostResponse;
import com.bist.mini.mypage.dto.ProfileImageUpdateResponse;
import com.bist.mini.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 마이페이지 API 컨트롤러
 */
@Tag(name = "MyPage", description = "마이페이지 API")
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

    @Operation(summary = "내 프로필 조회", description = "로그인된 회원의 프로필 정보를 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<MemberProfileResponse> getMyProfile(HttpServletRequest httpRequest) {
        Long memberId = extractMemberId(httpRequest);
        String baseUrl = extractBaseUrl(httpRequest);
        return ApiResponse.success(myPageService.getProfile(memberId, baseUrl));
    }

    // ── 내 게시글 목록 ─────────────────────────────────────────────────────────

    @Operation(summary = "내 게시글 목록 조회", description = "로그인된 회원이 작성한 게시글 목록을 조회합니다.")
    @GetMapping("/me/posts")
    public ApiResponse<List<MyPostResponse>> getMyPosts(HttpServletRequest httpRequest) {
        Long memberId = extractMemberId(httpRequest);
        return ApiResponse.success(myPageService.getMyPosts(memberId));
    }

    // ── 북마크한 게시글 목록 ────────────────────────────────────────────────────

    @Operation(summary = "북마크한 게시글 목록 조회", description = "로그인된 회원이 북마크한 게시글 목록을 조회합니다.")
    @GetMapping("/me/bookmarks")
    public ApiResponse<List<MyPostResponse>> getBookmarkedPosts(HttpServletRequest httpRequest) {
        Long memberId = extractMemberId(httpRequest);
        return ApiResponse.success(myPageService.getBookmarkedPosts(memberId));
    }

    // ── 닉네임 수정 ──────────────────────────────────────────────────────────

    @Operation(summary = "닉네임 수정", description = "로그인된 회원의 닉네임을 수정합니다.")
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

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경합니다.")
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

    @Operation(summary = "자기소개 수정", description = "로그인된 회원의 자기소개를 수정합니다.")
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

    @Operation(summary = "프로필 이미지 변경", description = "프로필 이미지를 업로드하고 URL을 반환합니다. (JPG·PNG·GIF·WEBP / 최대 5MB)")
    @PatchMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProfileImageUpdateResponse> updateProfileImage(
            HttpServletRequest httpRequest,
            @RequestPart("profileImage") MultipartFile profileImage
    ) {
        Long memberId = extractMemberId(httpRequest);
        String baseUrl = extractBaseUrl(httpRequest);
        return ApiResponse.success(myPageService.updateProfileImage(memberId, profileImage, baseUrl));
    }
}
