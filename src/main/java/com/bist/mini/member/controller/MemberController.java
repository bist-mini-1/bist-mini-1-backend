package com.bist.mini.member.controller;

import com.bist.mini.common.annotation.LoginMember;
import com.bist.mini.common.ApiResponse;
import com.bist.mini.member.dto.JoinRequest;
import com.bist.mini.member.dto.LoginRequest;
import com.bist.mini.member.dto.LoginResponse;
import com.bist.mini.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "본인 여부 확인", description = "요청된 memberId가 현재 로그인한 사용자인지 확인합니다.")
    @GetMapping("/{memberId}/is-me")
    public ApiResponse<Boolean> isMe(
            @LoginMember(required = false) Long currentMemberId,
            @Parameter(description = "확인할 회원 ID") @PathVariable("memberId") Long memberId) {
        boolean isMe = currentMemberId != null && currentMemberId.equals(memberId);
        return ApiResponse.success(isMe);
    }


    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return memberService.login(loginRequest);
    }

    @PostMapping("/join")
    public String join(@Valid @RequestBody JoinRequest joinRequest) {
        return memberService.join(joinRequest);
    }

    @GetMapping("/check-login-id")
    public boolean checkLoginId(@RequestParam String loginId) {
        return memberService.checkLoginIdDuplicate(loginId);
    }

    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return memberService.checkEmailDuplicate(email);
    }

    @GetMapping("/check-nickname")
    public boolean checkNickname(@RequestParam String nickname) {
        return memberService.checkNicknameDuplicate(nickname);
    }
}