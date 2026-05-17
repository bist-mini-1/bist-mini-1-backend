package com.bist.mini.member.service;

import com.bist.mini.common.jwt.JwtProvider;
import com.bist.mini.member.dao.MemberDao;
import com.bist.mini.member.dao.MemberInterestTagDao;
import com.bist.mini.member.dto.JoinRequest;
import com.bist.mini.member.dto.LoginRequest;
import com.bist.mini.member.dto.LoginResponse;
import com.bist.mini.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberDao memberDao;
    private final MemberInterestTagDao memberInterestTagDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest loginRequest) {
        Member member = memberDao.selectByLoginId(loginRequest.getLoginId());

        if (member == null) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.createToken(member);

        return new LoginResponse(
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

    @Transactional
    public String join(JoinRequest joinRequest) {
        if (memberDao.countByLoginId(joinRequest.getLoginId()) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (memberDao.countByEmail(joinRequest.getEmail()) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (memberDao.countByNickname(joinRequest.getNickname()) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        String encodedPassword = passwordEncoder.encode(joinRequest.getPassword());
        joinRequest.setPassword(encodedPassword);

        memberDao.insertMember(joinRequest);

        if (joinRequest.getInterestTagIds() != null
                && !joinRequest.getInterestTagIds().isEmpty()) {

            List<Long> distinctTagIds = joinRequest.getInterestTagIds()
                    .stream()
                    .distinct()
                    .toList();

            int existingTagCount =
                    memberInterestTagDao.countExistingTags(distinctTagIds);

            if (existingTagCount != distinctTagIds.size()) {
                throw new IllegalArgumentException("존재하지 않는 관심 태그가 포함되어 있습니다.");
            }

            memberInterestTagDao.insertMemberInterestTags(
                    joinRequest.getMemberId(),
                    distinctTagIds
            );
        }

        return "회원가입이 완료되었습니다.";
    }
}