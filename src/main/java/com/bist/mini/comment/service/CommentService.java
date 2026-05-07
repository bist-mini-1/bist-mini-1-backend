package com.bist.mini.comment.service;

import com.bist.mini.comment.dao.CommentDao;
import com.bist.mini.comment.dto.CommentUpdateRequest;
import com.bist.mini.comment.entity.Comment;
import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 댓글 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentDao commentDao;

    /**
     * 게시글별 댓글 목록 조회
     */
    public List<Comment> getCommentsByPost(Long postId) {
        return commentDao.findByPostId(postId);
    }

    /**
     * 댓글 상세 조회
     */
    public Comment getCommentDetail(Long commentId) {
        Comment comment = commentDao.findById(commentId);
        if (comment == null) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }
        return comment;
    }

    /**
     * 댓글 등록
     */
    @Transactional
    public Comment createComment(Comment comment) {
        if (comment.getParentId() != null) {
            Comment parent = getCommentDetail(comment.getParentId());
            
            // 삭제된 댓글에는 답글을 달 수 없음
            if (parent.isDeleted()) {
                throw new CustomException(ErrorCode.COMMENT_ALREADY_DELETED);
            }

            // 대댓글의 대댓글(2단계 이상)은 허용하지 않음
            if (parent.getParentId() != null) {
                throw new CustomException(ErrorCode.COMMENT_REPLY_DEPTH_EXCEEDED);
            }
        }

        commentDao.insert(comment);
        return getCommentDetail(comment.getCommentId());
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public Comment updateComment(Long commentId, CommentUpdateRequest request, Long memberId) {
        Comment comment = getCommentDetail(commentId);

        if (comment.isDeleted()) {
            throw new CustomException(ErrorCode.COMMENT_ALREADY_DELETED);
        }
        if (!comment.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        comment.setContent(request.getContent());
        commentDao.update(comment);

        return getCommentDetail(commentId);
    }

    /**
     * 댓글 삭제 (논리 삭제) - 자식 댓글까지 모두 삭제하고 삭제된 목록 반환
     */
    @Transactional
    public List<Comment> deleteComment(Long commentId, Long memberId) {
        Comment rootComment = getCommentDetail(commentId);

        if (!rootComment.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        // 삭제 전 데이터 캐싱 (N+1 쿼리 방지 및 불필요한 재조회 제거)
        List<Comment> deletedList = commentDao.findCommentsToDelete(commentId);

        // 일괄 논리 삭제
        commentDao.deleteCommentAndChildren(commentId);

        return deletedList;
    }
}
