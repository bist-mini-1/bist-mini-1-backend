package com.bist.mini.mypage.dao;

import com.bist.mini.mypage.entity.MemberProfile;
import com.bist.mini.mypage.entity.ProfileImage;
import com.bist.mini.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 마이페이지 데이터 접근 객체 (MyBatis Mapper)
 */
@Mapper
public interface MyPageDao {

    /** 회원 프로필 조회 */
    MemberProfile selectProfileByMemberId(@Param("memberId") Long memberId);

    /** 비밀번호(암호화된) 조회 - 현재 비밀번호 검증용 */
    String selectPasswordByMemberId(@Param("memberId") Long memberId);

    /** 닉네임 수정 */
    int updateNickname(@Param("memberId") Long memberId, @Param("nickname") String nickname);

    /** 비밀번호 수정 */
    int updatePassword(@Param("memberId") Long memberId, @Param("encodedPassword") String encodedPassword);

    /** 자기소개 수정 */
    int updateBio(@Param("memberId") Long memberId, @Param("bio") String bio);

    /** 프로필 이미지(BLOB) 저장 */
    int updateProfileImage(@Param("memberId") Long memberId, @Param("imageData") byte[] imageData);

    /** 프로필 이미지(BLOB) 조회 */
    ProfileImage selectProfileImageByMemberId(@Param("memberId") Long memberId);

    /** 내가 북마크한 게시글 목록 조회 (최신 북마크 순) */
    List<Post> selectBookmarkedPosts(@Param("memberId") Long memberId);
}
