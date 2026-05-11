package com.bist.mini.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class JoinRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Pattern(
            regexp = "^[a-z0-9]{4,20}$",
            message = "아이디는 4~20자의 영문 소문자와 숫자만 사용할 수 있습니다."
    )
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).{8,20}$",
            message = "비밀번호는 8~20자이며 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Pattern(
            regexp = "^[가-힣a-zA-Z0-9]{2,20}$",
            message = "닉네임은 2~20자의 한글, 영문, 숫자만 사용할 수 있습니다."
    )
    private String nickname;

    @Size(max = 1000, message = "자기소개는 1000자 이하로 입력해주세요.")
    private String bio;

    private List<Long> interestTagIds;
}