package com.bist.mini.notification.dto;

import com.bist.mini.notification.entity.Notification;
import com.bist.mini.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDto {

    private Long notificationId;

    private Long receiverId;

    private Long senderId;

    private String senderNickname;

    private Long postId;

    private Long commentId;

    private NotificationType type;

    private String message;

    private String isRead;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
                .notificationId(notification.getNotificationId())
                .receiverId(notification.getReceiverId())
                .senderId(notification.getSenderId())
                .senderNickname(notification.getSenderNickname())
                .postId(notification.getPostId())
                .commentId(notification.getCommentId())
                .type(notification.getType())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
