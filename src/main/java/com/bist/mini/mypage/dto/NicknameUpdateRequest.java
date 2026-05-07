package com.bist.mini.mypage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * PATCH /api/members/me/nickname 요청 DTO
 */
@Data
public class NicknameUpdateRequest {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Pattern(
            regexp = "^[가-힣a-zA-Z0-9]{2,20}$",
            message = "닉네임은 2~20자의 한글, 영문, 숫자만 사용할 수 있습니다."
    )
    private String nickname;
}
