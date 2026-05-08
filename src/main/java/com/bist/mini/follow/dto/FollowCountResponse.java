package com.bist.mini.follow.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 팔로워/팔로잉 수 조회 응답 DTO
 */
@Getter
@Builder
public class FollowCountResponse {

    private long followerCount;   // 나를 팔로우하는 사람 수
    private long followingCount;  // 내가 팔로우하는 사람 수
}
