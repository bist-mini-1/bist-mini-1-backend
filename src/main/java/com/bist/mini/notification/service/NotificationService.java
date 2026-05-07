package com.bist.mini.notification.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.notification.dao.NotificationDao;
import com.bist.mini.notification.entity.Notification;
import com.bist.mini.notification.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationDao notificationDao;

    /**
     * 회원의 알림 목록 조회
     */
    public List<Notification> getNotifications(Long memberId) {
        return notificationDao.findByReceiverId(memberId);
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void markAsRead(Long notificationId, Long memberId) {
        Notification notification = notificationDao.findById(notificationId);
        if (notification == null) {
            throw new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!notification.getReceiverId().equals(memberId)) {
            throw new CustomException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
        }
        notificationDao.markAsRead(notificationId);
    }

    /**
     * 알림 생성 (댓글, 좋아요, 팔로우 시 호출)
     */
    @Transactional
    public void createNotification(Long receiverId, Long senderId, Long postId, Long commentId, NotificationType type, String message) {
        // 본인이 본인 글에 액션을 취한 경우 알림 제외
        if (receiverId.equals(senderId)) {
            return;
        }

        // 중복 알림 방지 (팔로우 등, 읽지 않은 동일 타입 알림이 있으면 생성 안함)
        if (type == NotificationType.FOLLOW) {
            int count = notificationDao.countDuplicate(receiverId, senderId, type.name(), null);
            if (count > 0) return;
        }

        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .postId(postId)
                .commentId(commentId)
                .type(type)
                .message(message)
                .build();

        notificationDao.insert(notification);
    }
}
