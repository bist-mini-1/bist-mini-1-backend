package com.bist.mini.member.dao;

import com.bist.mini.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberDao {

    Member selectByLoginId(@Param("loginId") String loginId);
}