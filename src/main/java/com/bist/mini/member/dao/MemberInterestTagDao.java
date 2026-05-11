package com.bist.mini.member.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberInterestTagDao {

    void insertMemberInterestTags(
            @Param("memberId") Long memberId,
            @Param("tagIds") List<Long> tagIds
    );

    int countInterestTagsByMemberId(@Param("memberId") Long memberId);

    int countExistingTags(@Param("tagIds") List<Long> tagIds);
}