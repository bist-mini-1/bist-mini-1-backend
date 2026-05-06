package com.bist.mini.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Schema(example = "kosa")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(example = "kosa")
    private String password;
}