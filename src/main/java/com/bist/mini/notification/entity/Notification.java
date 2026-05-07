package com.bist.mini.notification.entity;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 알림 엔티티
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Long notificationId;
    private Long receiverId;
    private Long senderId;
    private Long postId;
    private Long commentId;
    private NotificationType type;
    private String message;
    private String isRead; // 'Y', 'N'
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    // 보낸 사람 닉네임 (조회용 추가 필드)
    private String senderNickname;
}
