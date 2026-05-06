package com.bist.mini.comment.service;

import com.bist.mini.comment.dao.CommentDAO;
import com.bist.mini.comment.dto.CommentRequest;
import com.bist.mini.comment.dto.CommentUpdateRequest;
import com.bist.mini.comment.entity.Comment;
import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentDAO commentDAO;

    /**
     * 게시글별 댓글 목록 조회
     */
    public List<Comment> getCommentsByPost(Long postId) {
        return commentDAO.findByPostId(postId);
    }

    /**
     * 댓글 상세 조회
     */
    public Comment getCommentDetail(Long commentId) {
        Comment comment = commentDAO.findById(commentId);
        if (comment == null) {
            throw new CustomException("존재하지 않는 댓글 ID입니다.", ErrorCode.ENTITY_NOT_FOUND);
        }
        return comment;
    }

    /**
     * 댓글 등록
     */
    @Transactional
    public Comment createComment(CommentRequest request, Long memberId) {
        // 대댓글인 경우 부모 댓글 검증
        if (request.getParentId() != null) {
            Comment parent = getCommentDetail(request.getParentId());
            
            // 삭제된 댓글에는 답글을 달 수 없음
            if ("Y".equals(parent.getIsDeleted())) {
                throw new CustomException("삭제된 댓글에는 답글을 달 수 없습니다.", ErrorCode.INVALID_INPUT_VALUE);
            }
            
            // 대댓글의 대댓글(2단계 이상)은 허용하지 않음 (1단계까지만 허용)
            if (parent.getParentId() != null) {
                throw new CustomException("대댓글에는 답글을 달 수 없습니다. (1단계까지만 허용)", ErrorCode.INVALID_INPUT_VALUE);
            }
        }

        Comment comment = Comment.builder()
                .postId(request.getPostId())
                .memberId(memberId)
                .parentId(request.getParentId())
                .content(request.getContent())
                .build();
        
        commentDAO.insert(comment);
        
        return getCommentDetail(comment.getCommentId());
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public Comment updateComment(Long commentId, CommentUpdateRequest request, Long memberId) {
        Comment comment = getCommentDetail(commentId);
        
        // 삭제된 댓글 수정 불가
        if ("Y".equals(comment.getIsDeleted())) {
            throw new CustomException("삭제된 댓글은 수정할 수 없습니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!comment.getMemberId().equals(memberId)) {
            throw new CustomException("작성자만 수정할 수 있습니다.", ErrorCode.INVALID_INPUT_VALUE);
        }
        
        comment.setContent(request.getContent());
        commentDAO.update(comment);
        
        // 수정된 데이터(updatedAt 등)를 다시 조회하여 반환
        return getCommentDetail(commentId);
    }

    /**
     * 댓글 삭제 (논리 삭제) - 자식 댓글까지 모두 삭제하고 삭제된 목록 반환
     */
    @Transactional
    public List<Comment> deleteComment(Long commentId, Long memberId) {
        Comment rootComment = getCommentDetail(commentId);
        
        if (!rootComment.getMemberId().equals(memberId)) {
            throw new CustomException("작성자만 삭제할 수 있습니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        List<Comment> deletedList = new ArrayList<>();
        // 재귀적으로 자식 댓글들 삭제 처리 및 목록 수집
        collectAndRecursiveDelete(commentId, deletedList);
        
        return deletedList;
    }

    /**
     * 하위 댓글을 재귀적으로 찾아 삭제하고 리스트에 추가하는 헬퍼 메서드
     */
    private void collectAndRecursiveDelete(Long commentId, List<Comment> deletedList) {
        // 자식들 조회
        List<Comment> children = commentDAO.findByParentId(commentId);
        
        for (Comment child : children) {
            // 자식의 자식부터 삭제 처리
            collectAndRecursiveDelete(child.getCommentId(), deletedList);
        }
        
        // 본인 삭제 처리
        commentDAO.delete(commentId);
        
        // 삭제 처리된 상태의 최신 데이터를 조회하여 리스트에 추가
        deletedList.add(commentDAO.findById(commentId));
    }
}
