package com.bist.mini.member.service;

import com.bist.mini.common.jwt.JwtProvider;
import com.bist.mini.member.dao.MemberDao;
import com.bist.mini.member.dto.LoginRequestDto;
import com.bist.mini.member.dto.LoginResponseDto;
import com.bist.mini.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Member member = memberDao.selectByLoginId(loginRequestDto.getLoginId());

        if (member == null) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.createToken(member);

        return new LoginResponseDto(
                accessToken,
                "Bearer",
                member.getMemberId(),
                member.getLoginId(),
                member.getNickname()
        );
    }
}