package com.bist.mini.follow.dao;

import com.bist.mini.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 팔로우 데이터 접근 객체 (MyBatis Mapper)
 */
@Mapper
public interface FollowDao {

    /** 팔로우 관계 존재 여부 확인 */
    int countFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /** 팔로우 추가 */
    void insertFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /** 팔로우 취소 */
    void deleteFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /** 나를 팔로우하는 사람 수 (팔로워 수) */
    long countFollowers(@Param("memberId") Long memberId);

    /** 내가 팔로우하는 사람 수 (팔로잉 수) */
    long countFollowings(@Param("memberId") Long memberId);

    /** 내가 팔로우한 사용자들의 게시글 목록 조회 */
    List<Post> selectFollowingPosts(@Param("memberId") Long memberId);
}
