package com.bist.mini.mypage.entity;

import lombok.Data;

/**
 * 프로필 이미지 BLOB 조회용 래퍼 엔티티
 */
@Data
public class ProfileImage {
    private byte[] imageData;
}
