package com.bist.mini.comment.dao;

import com.bist.mini.comment.entity.CommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * 댓글 좋아요 데이터 접근 객체
 */
@Mapper
public interface CommentLikeDao {
    // 좋아요 추가
    void insertLike(CommentLike commentLike);

    // 좋아요 삭제 (취소)
    void deleteLike(@Param("commentId") Long commentId, @Param("memberId") Long memberId);

    // 좋아요 여부 확인
    int existsLike(@Param("commentId") Long commentId, @Param("memberId") Long memberId);

    // 댓글별 좋아요 수 조회
    int countByCommentId(Long commentId);
}
