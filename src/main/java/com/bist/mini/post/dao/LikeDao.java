package com.bist.mini.post.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeDao {
    int countLike(@Param("postId") Long postId, @Param("memberId") Long memberId);
    void insertLike(@Param("postId") Long postId, @Param("memberId") Long memberId);
    void deleteLike(@Param("postId") Long postId, @Param("memberId") Long memberId);
    
    // Post 테이블의 like_count 업데이트를 위한 메서드 (필요시 PostDao에서 호출 가능하나 편의상 여기 추가하거나 PostDao 메서드 활용)
    int updateLikeCount(@Param("postId") Long postId, @Param("diff") int diff);
}
