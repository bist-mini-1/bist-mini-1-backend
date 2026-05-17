package com.bist.mini.follow.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 팔로우 엔티티
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    private Long followId;
    private Long followerId;    // 팔로우를 하는 사람
    private Long followingId;   // 팔로우를 받는 사람
    private LocalDateTime createdAt;
}
