package com.bist.mini.post.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookmarkDao {
    int countBookmark(@Param("postId") Long postId, @Param("memberId") Long memberId);
    void insertBookmark(@Param("postId") Long postId, @Param("memberId") Long memberId);
    void deleteBookmark(@Param("postId") Long postId, @Param("memberId") Long memberId);
}
