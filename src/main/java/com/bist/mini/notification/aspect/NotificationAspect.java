package com.bist.mini.notification.aspect;

import com.bist.mini.comment.entity.Comment;
import com.bist.mini.notification.entity.NotificationType;
import com.bist.mini.notification.service.NotificationService;
import com.bist.mini.post.dao.PostQueryDao;
import com.bist.mini.post.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationAspect {

    private final NotificationService notificationService;
    private final PostQueryDao postQueryDao;

    /**
     * 댓글 생성 시점
     */
    @Pointcut("execution(* com.bist.mini.comment.service.CommentService.createComment(..))")
    public void commentCreated() {}

    /**
     * 좋아요 토글 시점
     */
    @Pointcut("execution(* com.bist.mini.post.service.LikeService.toggleLike(..))")
    public void likeToggled() {}

    /**
     * 댓글 생성 후 알림 처리
     */
    @AfterReturning(pointcut = "commentCreated()", returning = "comment")
    public void afterCommentCreated(Comment comment) {
        log.info("NotificationAspect: After comment created. CommentId: {}", comment != null ? comment.getCommentId() : "null");
        try {
            if (comment == null) return;

            Post post = postQueryDao.findById(comment.getPostId());
            if (post == null) {
                log.warn("NotificationAspect: Post not found for ID: {}", comment.getPostId());
                return;
            }

            log.info("NotificationAspect: Sending comment notification to member: {}", post.getMemberId());
            notificationService.createNotification(
                    post.getMemberId(), // receiver
                    comment.getMemberId(), // sender
                    post.getPostId(),
                    comment.getCommentId(),
                    NotificationType.COMMENT,
                    String.format("'%s' 게시글에 새로운 댓글이 달렸습니다.", post.getTitle())
            );
        } catch (Exception e) {
            log.error("Failed to create comment notification", e);
        }
    }

    /**
     * 좋아요 토글 후 알림 처리 (좋아요 추가된 경우에만)
     */
    @AfterReturning(pointcut = "likeToggled() && args(postId, memberId)", returning = "isLiked")
    public void afterLikeToggled(Long postId, Long memberId, boolean isLiked) {
        log.info("NotificationAspect: After like toggled. PostId: {}, MemberId: {}, isLiked: {}", postId, memberId, isLiked);
        try {
            if (!isLiked) return; // 좋아요 취소인 경우 알림 안함

            Post post = postQueryDao.findById(postId);
            if (post == null) {
                log.warn("NotificationAspect: Post not found for ID: {}", postId);
                return;
            }

            log.info("NotificationAspect: Sending like notification to member: {}", post.getMemberId());
            notificationService.createNotification(
                    post.getMemberId(), // receiver
                    memberId, // sender
                    postId,
                    null,
                    NotificationType.LIKE,
                    String.format("'%s' 게시글을 좋아합니다.", post.getTitle())
            );
        } catch (Exception e) {
            log.error("Failed to create like notification", e);
        }
    }
}
