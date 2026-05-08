package com.bist.mini.follow.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 팔로워 / 팔로잉 목록 + 수 응답 DTO
 */
@Getter
@Builder
public class FollowListResponse {

    private long count;
    private List<FollowUserResponse> users;
}
