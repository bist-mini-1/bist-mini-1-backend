package com.bist.mini.comment.service;

import com.bist.mini.comment.dao.CommentDao;
import com.bist.mini.comment.dao.CommentLikeDao;
import com.bist.mini.comment.dto.CommentResponse;
import com.bist.mini.comment.dto.CommentUpdateRequest;
import com.bist.mini.comment.entity.Comment;
import com.bist.mini.comment.entity.CommentLike;
import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.member.dao.MemberDao;
import com.bist.mini.member.entity.Member;
import com.bist.mini.notification.entity.NotificationType;
import com.bist.mini.notification.service.NotificationService;
import com.bist.mini.post.dao.PostQueryDao;
import com.bist.mini.post.entity.Post;
import com.bist.mini.comment.dto.CommentListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 댓글 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentDao commentDao;
    private final MemberDao memberDao;
    private final CommentLikeDao commentLikeDao;
    private final NotificationService notificationService;
    private final PostQueryDao postQueryDao;

    /**
     * 게시글별 댓글 목록 조회 (페이징 적용)
     */
    public CommentListResponse getCommentsByPost(Long postId, Long currentMemberId, int page, int size) {
        int offset = (page - 1) * size;
        List<Comment> comments = commentDao.findByPostId(postId, offset, size);
        int totalCount = commentDao.countByPostId(postId);
        
        // 게시글 정보 조회 (삭제 권한 체크용)
        Post post = postQueryDao.findById(postId);
        Long postAuthorId = (post != null) ? post.getMemberId() : null;

        // 베스트 댓글 조회 (1페이지일 때만 조회하거나 항상 조회)
        Comment bestCommentEntity = commentDao.findBestCommentByPostId(postId);
        CommentResponse bestComment = (bestCommentEntity != null) ? convertToResponse(bestCommentEntity, currentMemberId, postAuthorId) : null;

        List<CommentResponse> commentResponses = comments.stream()
                .map(comment -> convertToResponse(comment, currentMemberId, postAuthorId))
                .collect(Collectors.toList());

        return CommentListResponse.of(commentResponses, totalCount, bestComment);
    }

    /**
     * Comment 엔티티를 CommentResponse DTO로 변환하는 내부 헬퍼 메서드
     */
    private CommentResponse convertToResponse(Comment comment, Long currentMemberId, Long postAuthorId) {
        String nickname = "알 수 없는 사용자";
        String profileImageUrl = null;
        
        Member author = memberDao.findById(comment.getMemberId());
        if (author != null) {
            nickname = author.getNickname();
            // profileImageUrl = author.getProfileImageUrl();
        }

        int likeCount = commentLikeDao.countByCommentId(comment.getCommentId());
        boolean isLiked = currentMemberId != null && commentLikeDao.existsLike(comment.getCommentId(), currentMemberId) > 0;

        // 본인 여부 및 삭제 권한 계산
        boolean isMine = currentMemberId != null && comment.getMemberId().equals(currentMemberId);
        boolean canDelete = isMine || (currentMemberId != null && currentMemberId.equals(postAuthorId));

        return CommentResponse.from(comment, nickname, profileImageUrl, likeCount, isLiked, isMine, canDelete);
    }

    /**
     * 댓글 좋아요 토글
     */
    @Transactional
    public boolean toggleLike(Long commentId, Long memberId) {
        Comment comment = getCommentDetail(commentId);
        boolean exists = commentLikeDao.existsLike(commentId, memberId) > 0;
        
        if (exists) {
            commentLikeDao.deleteLike(commentId, memberId);
            return false; // 좋아요 취소
        } else {
            commentLikeDao.insertLike(CommentLike.builder()
                    .commentId(commentId)
                    .memberId(memberId)
                    .build());
            
            // 본인 댓글이 아닌 경우에만 알림 발송
            if (!comment.getMemberId().equals(memberId)) {
                Member liker = memberDao.findById(memberId);
                String likerNickname = (liker != null) ? liker.getNickname() : "누군가";
                
                notificationService.createNotification(
                    comment.getMemberId(),
                    memberId,
                    comment.getPostId(),
                    comment.getCommentId(),
                    NotificationType.COMMENT_LIKE,
                    likerNickname + "님이 회원님의 댓글에 좋아요를 눌렀습니다."
                );
            }
            
            return true; // 좋아요 추가
        }
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

    @Transactional
    public List<Comment> deleteComment(Long commentId, Long memberId) {
        Comment rootComment = getCommentDetail(commentId);

        // 댓글 작성자이거나 게시글 작성자인 경우 삭제 가능
        boolean isAuthor = rootComment.getMemberId().equals(memberId);
        boolean isPostOwner = false;
        
        Post post = postQueryDao.findById(rootComment.getPostId());
        if (post != null && post.getMemberId().equals(memberId)) {
            isPostOwner = true;
        }

        if (!isAuthor && !isPostOwner) {
            throw new CustomException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        // 삭제 전 데이터 캐싱 (N+1 쿼리 방지 및 불필요한 재조회 제거)
        List<Comment> deletedList = commentDao.findCommentsToDelete(commentId);

        // 일괄 논리 삭제
        commentDao.deleteCommentAndChildren(commentId);

        return deletedList;
    }
 
    /**
     * 내 댓글 여부 확인
     */
    public boolean isMyComment(Long commentId, Long memberId) {
        if (memberId == null) return false;
        Comment comment = commentDao.findById(commentId);
        return comment != null && comment.getMemberId().equals(memberId);
    }
}
