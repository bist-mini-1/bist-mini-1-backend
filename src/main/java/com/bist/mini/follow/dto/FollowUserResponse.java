package com.bist.mini.follow.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * 팔로워 / 팔로잉 목록 조회 응답 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowUserResponse {

    private Long memberId;
    private String nickname;
    private String profileImage;
}
