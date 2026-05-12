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

    /** 회원의 관심 태그 ID 목록 조회 */
    List<Long> selectTagIdsByMemberId(@Param("memberId") Long memberId);

    /** 회원의 관심 태그 전체 삭제 */
    int deleteByMemberId(@Param("memberId") Long memberId);
}