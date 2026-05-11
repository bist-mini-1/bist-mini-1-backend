package com.bist.mini.mypage.dto;

import com.bist.mini.mypage.entity.MemberProfile;
import lombok.Builder;
import lombok.Getter;

/**
 * GET /api/members/me 응답 DTO
 */
@Getter
@Builder
public class MemberProfileResponse {

    private Long memberId;
    private String loginId;
    private String email;
    private String nickname;
    private String bio;
    private String profileImageUrl; // 절대 URL (baseUrl + 상대경로)

    public static MemberProfileResponse from(MemberProfile profile, String baseUrl) {
        String profileImageUrl = null;
        if (profile.getProfileImage() != null && profile.getProfileImage().length > 0) {
            profileImageUrl = baseUrl + new String(profile.getProfileImage());
        }
        return MemberProfileResponse.builder()
                .memberId(profile.getMemberId())
                .loginId(profile.getLoginId())
                .email(profile.getEmail())
                .nickname(profile.getNickname())
                .bio(profile.getBio())
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
