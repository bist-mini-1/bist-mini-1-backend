package com.bist.mini.notification.dto;

import com.bist.mini.notification.entity.Notification;
import com.bist.mini.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "알림 응답 DTO")
public class NotificationResponseDto {

    @Schema(description = "알림 ID")
    private Long notificationId;

    @Schema(description = "알림 수신자 ID")
    private Long receiverId;

    @Schema(description = "알림 발신자 ID")
    private Long senderId;

    @Schema(description = "알림 발신자 닉네임")
    private String senderNickname;

    @Schema(description = "관련 게시글 ID")
    private Long postId;

    @Schema(description = "관련 댓글 ID")
    private Long commentId;

    @Schema(description = "알림 타입")
    private NotificationType type;

    @Schema(description = "알림 메시지")
    private String message;

    @Schema(description = "읽음 여부")
    private String isRead;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "읽은 일시")
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
