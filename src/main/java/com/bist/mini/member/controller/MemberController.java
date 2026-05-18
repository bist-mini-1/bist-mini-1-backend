package com.bist.mini.member.controller;

import com.bist.mini.common.annotation.LoginMember;
import com.bist.mini.common.ApiResponse;
import com.bist.mini.member.dto.JoinRequest;
import com.bist.mini.member.dto.LoginRequest;
import com.bist.mini.member.dto.LoginResponse;
import com.bist.mini.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{memberId}/is-me")
    public ApiResponse<Boolean> isMe(
            @LoginMember(required = false) Long currentMemberId,
            @PathVariable("memberId") Long memberId) {
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