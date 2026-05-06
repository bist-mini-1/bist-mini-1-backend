package com.bist.mini.member.controller;

import com.bist.mini.member.dto.LoginRequestDto;
import com.bist.mini.member.dto.LoginResponseDto;
import com.bist.mini.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return memberService.login(loginRequestDto);
    }
}