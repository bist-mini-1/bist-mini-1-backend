package com.bist.mini.post.dao;

import com.bist.mini.post.dto.PostListResponse;
import com.bist.mini.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Post 데이터 접근 객체 (MyBatis Mapper)
 */
@Mapper
public interface PostDao {

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

   long countAll();

   List<String> selectTagNamesByPostId(@Param("postId") Long postId);

   List<PostListResponse> selectPostList(
           @Param("offset") int offset,
           @Param("size") int size
   );

   long countPostList();
}
