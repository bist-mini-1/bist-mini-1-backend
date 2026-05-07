package com.bist.mini.mypage.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * PATCH /api/members/me/bio 요청 DTO
 */
@Data
public class BioUpdateRequest {

    @Size(max = 1000, message = "자기소개는 1000자 이하로 입력해주세요.")
    private String bio;
}
