package com.bist.mini.member.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Member {
    private Long memberId;
    private String loginId;
    private String password;
    private String email;
    private String nickname;
    private String bio;
    private byte[] profileImage; // DB 저장된 이미지 바이너리 (BLOB)
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}