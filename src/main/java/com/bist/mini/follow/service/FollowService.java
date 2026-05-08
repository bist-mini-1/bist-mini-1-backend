package com.bist.mini.follow.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.follow.dao.FollowDao;
import com.bist.mini.follow.dto.FollowCountResponse;
import com.bist.mini.member.dao.MemberDao;
import com.bist.mini.member.entity.Member;
import com.bist.mini.mypage.dto.MyPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 팔로우 비즈니스 로직 서비스
 */
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowDao followDao;
    private final MemberDao memberDao;

    // ── 팔로우 ─────────────────────────────────────────────────────────────────

    @Transactional
    public void follow(Long followerId, Long followingId) {
        // 자기 자신 팔로우 방지
        if (followerId.equals(followingId)) {
            throw new CustomException(ErrorCode.SELF_FOLLOW_NOT_ALLOWED);
        }

        // 팔로우 대상 회원 존재 여부 확인
        Member target = memberDao.findById(followingId);
        if (target == null || !"ACTIVE".equals(target.getStatus())) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }

        // 중복 팔로우 방지
        if (followDao.countFollow(followerId, followingId) > 0) {
            throw new CustomException(ErrorCode.FOLLOW_ALREADY_EXISTS);
        }

        followDao.insertFollow(followerId, followingId);
    }

    // ── 팔로우 취소 ────────────────────────────────────────────────────────────

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        // 팔로우 관계가 없으면 예외
        if (followDao.countFollow(followerId, followingId) == 0) {
            throw new CustomException(ErrorCode.FOLLOW_NOT_FOUND);
        }

        followDao.deleteFollow(followerId, followingId);
    }

    // ── 팔로워/팔로잉 수 조회 ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public FollowCountResponse getFollowCount(Long memberId) {
        long followerCount = followDao.countFollowers(memberId);
        long followingCount = followDao.countFollowings(memberId);

        return FollowCountResponse.builder()
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }

    // ── 팔로워 수 조회 (나를 팔로우하는 사람 수) ──────────────────────────────────

    @Transactional(readOnly = true)
    public long getFollowerCount(Long memberId) {
        return followDao.countFollowers(memberId);
    }

    // ── 팔로잉 수 조회 (내가 팔로우하는 사람 수) ──────────────────────────────────

    @Transactional(readOnly = true)
    public long getFollowingCount(Long memberId) {
        return followDao.countFollowings(memberId);
    }

    // ── 팔로우한 사용자 게시글 조회 ──────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MyPostResponse> getFollowingPosts(Long memberId) {
        return followDao.selectFollowingPosts(memberId).stream()
                .map(MyPostResponse::from)
                .toList();
    }
}
