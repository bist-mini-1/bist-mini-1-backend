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

   int softDeleteCommentsByPostId(Long postId);

   int softDeletePostTagsByPostId(Long postId);

   int insertPostTag(@Param("postId") Long postId, @Param("tagId") Long tagId);

   int softDeletePostLikesByPostId(Long postId);

   int softDeleteBookmarksByPostId(Long postId);

   int softDeleteAttachmentsByPostId(Long postId);

   // 페이지네이션 및 필터링
   List<Post> findAllWithPage(
         @Param("offset") int offset, 
         @Param("limit") int limit, 
         @Param("category") String category);

   long countAll(@Param("category") String category);

   // 태그별 조회
   List<Post> findByTag(
         @Param("tagName") String tagName, 
         @Param("offset") int offset, 
         @Param("limit") int limit);

   long countByTag(@Param("tagName") String tagName);

   // 좋아요/북마크 상태 확인
   int countLike(@Param("postId") Long postId, @Param("memberId") Long memberId);

   int countBookmark(@Param("postId") Long postId, @Param("memberId") Long memberId);

}
