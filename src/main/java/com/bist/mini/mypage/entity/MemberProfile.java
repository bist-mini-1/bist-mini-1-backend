package com.bist.mini.mypage.entity;

import lombok.Data;

/**
 * 마이페이지 프로필 조회용 엔티티 (members 테이블 매핑)
 */
@Data
public class MemberProfile {
    private Long memberId;
    private String loginId;
    private String email;
    private String nickname;
    private String bio;
    private byte[] profileImage; // DB 저장 경로 (예: /uploads/profile/uuid.jpg)
}
