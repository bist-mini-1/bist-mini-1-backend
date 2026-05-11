package com.bist.mini.comment.dao;

import com.bist.mini.comment.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 댓글 데이터 접근 객체 (MyBatis Mapper)
 */
@Mapper
public interface CommentDao {

    /**
     * 게시글별 댓글 목록 조회 (페이징 적용)
     */
    List<Comment> findByPostId(
            @Param("postId") Long postId, 
            @Param("offset") int offset, 
            @Param("limit") int limit
    );

    Comment findById(Long commentId);

    List<Comment> findByParentId(Long parentId);

    void insert(Comment comment);

    void update(Comment comment);

    List<Comment> findCommentsToDelete(Long commentId);

    void deleteCommentAndChildren(Long commentId);

    /**
     * 게시글별 전체 댓글 수 조회
     */
    int countByPostId(@Param("postId") Long postId);

    /**
     * 게시글별 베스트 댓글 조회 (좋아요 가장 많은 것)
     */
    Comment findBestCommentByPostId(@Param("postId") Long postId);
}
