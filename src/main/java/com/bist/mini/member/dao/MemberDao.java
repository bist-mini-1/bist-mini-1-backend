package com.bist.mini.member.dao;

import com.bist.mini.member.dto.JoinRequest;
import com.bist.mini.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberDao {

    Member selectByLoginId(@Param("loginId") String loginId);

    Member findById(@Param("memberId") Long memberId);

    int countByLoginId(@Param("loginId") String loginId);

    int countByEmail(@Param("email") String email);

    int countByNickname(@Param("nickname") String nickname);

    int insertMember(JoinRequest joinRequest);
}