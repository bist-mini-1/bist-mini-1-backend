package com.bist.mini.post.dao;

import com.bist.mini.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 게시글 상태 변경 전용 데이터 접근 객체 (MyBatis Mapper)
 */
@Mapper
public interface PostDao {

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
}
