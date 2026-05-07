package com.bist.mini.mypage.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.member.dao.MemberDao;
import com.bist.mini.mypage.dao.MyPageDao;
import com.bist.mini.mypage.dto.BioUpdateRequest;
import com.bist.mini.mypage.dto.MemberProfileResponse;
import com.bist.mini.mypage.dto.NicknameUpdateRequest;
import com.bist.mini.mypage.dto.PasswordUpdateRequest;
import com.bist.mini.mypage.dto.MyPostResponse;
import com.bist.mini.mypage.dto.ProfileImageUpdateResponse;
import com.bist.mini.mypage.entity.MemberProfile;
import com.bist.mini.post.dao.PostDao;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageDao myPageDao;
    private final MemberDao memberDao;
    private final PostDao postDao;
    private final PasswordEncoder passwordEncoder;

    private final Path profileUploadRoot = Paths.get("uploads", "profile").toAbsolutePath().normalize();

    @PostConstruct
    public void initUploadDir() {
        try {
            Files.createDirectories(profileUploadRoot);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

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

        if (file.getSize() > 5L * 1024 * 1024) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 기존 프로필 이미지 파일 삭제
        MemberProfile existing = myPageDao.selectProfileByMemberId(memberId);
        if (existing != null && existing.getProfileImage() != null && !existing.getProfileImage().isEmpty()) {
            try {
                String oldFileName = Paths.get(existing.getProfileImage()).getFileName().toString();
                Files.deleteIfExists(profileUploadRoot.resolve(oldFileName));
            } catch (IOException ignored) {
                // 기존 파일 삭제 실패는 무시하고 진행
            }
        }

        // 저장 파일명 생성 (contentType 기반 확장자 화이트리스트)
        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/gif"  -> ".gif";
            case "image/webp" -> ".webp";
            default -> throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        };
        String storedName = UUID.randomUUID() + extension;
        Path target = profileUploadRoot.resolve(storedName);

        try {
            file.transferTo(target);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // DB 저장 경로: 상대 URL
        String relativeUrl = "/uploads/profile/" + storedName;
        myPageDao.updateProfileImage(memberId, relativeUrl);

        return new ProfileImageUpdateResponse(baseUrl + relativeUrl);
    }

    // ── 내 게시글 목록 ─────────────────────────────────────────────────────────

    public List<MyPostResponse> getMyPosts(Long memberId) {
        return postDao.findByMemberId(memberId).stream()
                .map(MyPostResponse::from)
                .toList();
    }
}
