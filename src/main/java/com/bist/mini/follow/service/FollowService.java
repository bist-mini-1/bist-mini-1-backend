package com.bist.mini.follow.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.follow.dao.FollowDao;
import com.bist.mini.follow.dto.FollowCountResponse;
import com.bist.mini.follow.dto.FollowListResponse;
import com.bist.mini.follow.dto.FollowUserResponse;
import com.bist.mini.member.dao.MemberDao;
import com.bist.mini.member.entity.Member;
import com.bist.mini.mypage.dto.MyPostResponse;
import com.bist.mini.notification.entity.NotificationType;
import com.bist.mini.notification.service.NotificationService;
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
    private final NotificationService notificationService;

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

        // 중복 팔로우 방지 (이미 팔로우 중이면 무시)
        if (followDao.countFollow(followerId, followingId) > 0) {
            return;
        }

        followDao.insertFollow(followerId, followingId);

        // 알림 생성
        Member follower = memberDao.findById(followerId);
        String message = follower.getNickname() + "님이 회원님을 팔로우했습니다.";
        notificationService.createNotification(followingId, followerId, null, null, NotificationType.FOLLOW, message);
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

    // ── 팔로워 목록 조회 (나를 팔로우하는 사람 목록) ──────────────────────────────

    @Transactional(readOnly = true)
    public FollowListResponse getFollowers(Long memberId, String baseUrl) {
        List<FollowUserResponse> users = followDao.selectFollowers(memberId);
        return FollowListResponse.builder()
                .count(users.size())
                .users(users)
                .build();
    }

    // ── 팔로잉 목록 조회 (내가 팔로우하는 사람 목록) ──────────────────────────────

    @Transactional(readOnly = true)
    public FollowListResponse getFollowings(Long memberId, String baseUrl) {
        List<FollowUserResponse> users = followDao.selectFollowings(memberId);
        return FollowListResponse.builder()
                .count(users.size())
                .users(users)
                .build();
    }

    // ── 팔로우한 사용자 게시글 조회 ──────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MyPostResponse> getFollowingPosts(Long memberId) {
        return followDao.selectFollowingPosts(memberId).stream()
                .map(MyPostResponse::from)
                .toList();
    }

}
