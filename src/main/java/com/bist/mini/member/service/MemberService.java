package com.bist.mini.member.service;

import com.bist.mini.common.jwt.JwtProvider;
import com.bist.mini.member.dao.MemberDao;
import com.bist.mini.member.dto.JoinRequestDto;
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

    public boolean checkLoginIdDuplicate(String loginId) {
        return memberDao.countByLoginId(loginId) > 0;
    }

    public boolean checkEmailDuplicate(String email) {
        return memberDao.countByEmail(email) > 0;
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return memberDao.countByNickname(nickname) > 0;
    }

    public String join(JoinRequestDto joinRequestDto) {
        if (memberDao.countByLoginId(joinRequestDto.getLoginId()) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (memberDao.countByEmail(joinRequestDto.getEmail()) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (memberDao.countByNickname(joinRequestDto.getNickname()) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        String encodedPassword = passwordEncoder.encode(joinRequestDto.getPassword());
        joinRequestDto.setPassword(encodedPassword);

        memberDao.insertMember(joinRequestDto);

        return "회원가입이 완료되었습니다.";
    }
}