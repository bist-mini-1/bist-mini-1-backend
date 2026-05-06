package com.bist.mini.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String tokenType;
    private Long memberId;
    private String loginId;
    private String nickname;
}