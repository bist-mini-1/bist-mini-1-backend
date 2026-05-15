package com.bist.mini.mypage.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.member.dao.MemberDao;
import com.bist.mini.member.dao.MemberInterestTagDao;
import com.bist.mini.mypage.dao.MyPageDao;
import com.bist.mini.mypage.dto.BioUpdateRequest;
import com.bist.mini.mypage.dto.InterestTagUpdateRequest;
import com.bist.mini.mypage.dto.MemberProfileResponse;
import com.bist.mini.mypage.dto.NicknameUpdateRequest;
import com.bist.mini.mypage.dto.PasswordUpdateRequest;
import com.bist.mini.mypage.dto.MyPostResponse;
import com.bist.mini.mypage.dto.ProfileImageUpdateResponse;
import com.bist.mini.mypage.entity.MemberProfile;
import com.bist.mini.mypage.entity.ProfileImage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageDao myPageDao;
    private final MemberDao memberDao;
    private final MemberInterestTagDao memberInterestTagDao;
    private final com.bist.mini.post.dao.TagDao tagDao;
    private final PasswordEncoder passwordEncoder;

    // ── 프로필 조회 ──────────────────────────────────────────────────────────

    public MemberProfileResponse getProfile(Long memberId, String baseUrl) {
        MemberProfile profile = myPageDao.selectProfileByMemberId(memberId);
        if (profile == null) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }
        return MemberProfileResponse.from(profile, baseUrl);
    }

    // ── 닉네임 수정 ──────────────────────────────────────────────────────────

    @Transactional
    public void updateNickname(Long memberId, NicknameUpdateRequest request) {
        MemberProfile current = myPageDao.selectProfileByMemberId(memberId);
        if (current == null) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }

        // 현재 닉네임과 다를 때만 중복 체크
        if (!current.getNickname().equals(request.getNickname())) {
            if (memberDao.countByNickname(request.getNickname()) > 0) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
        }

        myPageDao.updateNickname(memberId, request.getNickname());
    }

    // ── 비밀번호 변경 ─────────────────────────────────────────────────────────

    @Transactional
    public void updatePassword(Long memberId, PasswordUpdateRequest request) {
        String encodedCurrent = myPageDao.selectPasswordByMemberId(memberId);
        if (encodedCurrent == null) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), encodedCurrent)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String newEncoded = passwordEncoder.encode(request.getNewPassword());
        myPageDao.updatePassword(memberId, newEncoded);
    }

    // ── 자기소개 수정 ─────────────────────────────────────────────────────────

    @Transactional
    public void updateBio(Long memberId, BioUpdateRequest request) {
        MemberProfile current = myPageDao.selectProfileByMemberId(memberId);
        if (current == null) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }
        String bio = (request.getBio() != null) ? request.getBio() : "";
        myPageDao.updateBio(memberId, bio);
    }

    // ── 프로필 이미지 업로드 ───────────────────────────────────────────────────

    @Transactional
    public ProfileImageUpdateResponse updateProfileImage(Long memberId, MultipartFile file, String baseUrl) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // contentType 화이트리스트 검증
        switch (contentType) {
            case "image/jpeg", "image/png", "image/gif", "image/webp" -> {
                /* 허용 */ }
            default -> throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (file.getSize() > 5L * 1024 * 1024) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        byte[] imageData;
        try {
            imageData = file.getBytes();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        myPageDao.updateProfileImage(memberId, imageData);

        String profileImageUrl = baseUrl + "/api/members/" + memberId + "/profile-image";
        return new ProfileImageUpdateResponse(profileImageUrl);
    }

    // ── 프로필 이미지 조회 (BLOB → byte[]) ────────────────────────────────────

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getProfileImage(Long memberId) {
        ProfileImage result = myPageDao.selectProfileImageByMemberId(memberId);
        byte[] imageData = (result != null) ? result.getImageData() : null;
        if (imageData == null || imageData.length == 0) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }

        MediaType mediaType = detectMediaType(imageData);
        if (mediaType == null) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(java.util.Objects.requireNonNull(mediaType))
                .body(imageData);
    }

    private MediaType detectMediaType(byte[] bytes) {
        if (bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF) {
            return MediaType.IMAGE_JPEG;
        }
        if (bytes.length >= 4
                && (bytes[0] & 0xFF) == 0x89
                && bytes[1] == 'P'
                && bytes[2] == 'N'
                && bytes[3] == 'G') {
            return MediaType.IMAGE_PNG;
        }
        if (bytes.length >= 3
                && bytes[0] == 'G'
                && bytes[1] == 'I'
                && bytes[2] == 'F') {
            return MediaType.IMAGE_GIF;
        }
        // WEBP: RIFF....WEBP
        if (bytes.length >= 12
                && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P') {
            return MediaType.parseMediaType("image/webp");
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    // ── 내 게시글 목록 ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MyPostResponse> getMyPosts(Long memberId) {
        List<MyPostResponse> posts = myPageDao.selectMyPosts(memberId);
        attachTags(posts);
        return posts;
    }

    // ── 타인 프로필 조회 ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public MemberProfileResponse getUserProfile(Long memberId, String baseUrl) {
        MemberProfile profile = myPageDao.selectProfileByMemberId(memberId);
        if (profile == null) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }
        return MemberProfileResponse.from(profile, baseUrl);
    }

    // ── 타인 공개 게시글 목록 ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MyPostResponse> getUserPosts(Long memberId) {
        List<MyPostResponse> posts = myPageDao.selectUserPosts(memberId);
        attachTags(posts);
        return posts;
    }

    // ── 북마크한 게시글 목록 ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MyPostResponse> getBookmarkedPosts(Long memberId) {
        List<MyPostResponse> posts = myPageDao.selectBookmarkedPosts(memberId);
        attachTags(posts);
        return posts;
    }

    private void attachTags(List<MyPostResponse> posts) {
        if (posts == null || posts.isEmpty()) {
            return;
        }

        List<Long> postIds = posts.stream().map(MyPostResponse::getPostId).toList();
        List<com.bist.mini.post.dto.PostTag> postTags = tagDao.findTagsByPostIds(postIds);

        Map<Long, List<String>> tagsByPostId = postTags.stream()
                .collect(Collectors.groupingBy(
                        com.bist.mini.post.dto.PostTag::getPostId,
                        Collectors.mapping(pt -> pt.getTag().getName(), Collectors.toList())));

        posts.forEach(post -> post.setTags(tagsByPostId.getOrDefault(post.getPostId(), new ArrayList<>())));
    }

    // ── 관심 태그 조회 ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Long> getInterestTags(Long memberId) {
        return memberInterestTagDao.selectTagIdsByMemberId(memberId);
    }

    // ── 관심 태그 수정 ────────────────────────────────────────────────────────

    @Transactional
    public void updateInterestTags(Long memberId, InterestTagUpdateRequest request) {
        // 기존 태그 전체 삭제
        memberInterestTagDao.deleteByMemberId(memberId);

        List<Long> tagIds = request.getTagIds();
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }

        List<Long> distinctTagIds = tagIds.stream().distinct().toList();

        // 유효한 태그인지 검증
        int existingCount = memberInterestTagDao.countExistingTags(distinctTagIds);
        if (existingCount != distinctTagIds.size()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        memberInterestTagDao.insertMemberInterestTags(memberId, distinctTagIds);
    }
}
