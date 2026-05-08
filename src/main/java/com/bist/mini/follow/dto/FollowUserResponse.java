package com.bist.mini.follow.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 팔로워 / 팔로잉 목록 조회 응답 DTO
 */
@Getter
@Builder
public class FollowUserResponse {

    private Long memberId;
    private String nickname;
    private String profileImage;
}
