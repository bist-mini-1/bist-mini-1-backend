package com.bist.mini.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * PATCH /api/members/me/profile-image 응답 DTO
 */
@Data
@AllArgsConstructor
public class ProfileImageUpdateResponse {
    private String profileImageUrl; // 절대 URL (baseUrl + /uploads/profile/uuid.ext)
}
