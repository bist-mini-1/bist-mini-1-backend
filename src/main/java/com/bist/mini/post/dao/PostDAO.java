package com.bist.mini.post.dao;

import com.bist.mini.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Post 데이터 접근 객체 (MyBatis Mapper)
 */
@Mapper
public interface PostDAO {

   List<Post> findAll();

   List<Post> findByMemberId(Long memberId);

   List<Post> findTempByMemberId(Long memberId);

   Post findById(Long id);

   void insert(Post post);

   int updateViewCount(Long postId);

   int updatePost(Post post);

   int softDeletePost(@Param("postId") Long postId, @Param("memberId") Long memberId);

   // 관련 테이블 소프트 삭제 메서드
   int softDeleteCommentsByPostId(Long postId);

   int softDeletePostTagsByPostId(Long postId);

   int softDeletePostLikesByPostId(Long postId);

   int softDeleteBookmarksByPostId(Long postId);

   int softDeleteAttachmentsByPostId(Long postId);

}