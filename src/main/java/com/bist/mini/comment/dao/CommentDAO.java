package com.bist.mini.comment.dao;

import com.bist.mini.comment.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 댓글 데이터 접근 객체 (MyBatis Mapper)
 */
@Mapper
public interface CommentDAO {

    List<Comment> findByPostId(Long postId);

    Comment findById(Long commentId);

    List<Comment> findByParentId(Long parentId);

    void insert(Comment comment);

    void update(Comment comment);

    void delete(Long commentId);

}
