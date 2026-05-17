package com.bist.mini.chat.dto;

import com.bist.mini.chat.entity.ChatMessage;
import com.bist.mini.chat.entity.ChatMessageType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 응답 DTO
 */
@Getter
@Builder
public class ChatMessageResponse {
    private Long messageId;
    private Long roomId;
    private Long senderId;
    private String senderNickname;
    private String senderProfileImage;
    private ChatMessageType messageType;
    private String content;
    private LocalDateTime createdAt;
    private int unreadCount; // 안 읽은 인원 수 (1:1에서는 0 또는 1)
    private boolean isMine;
    private boolean isDeleted;

    public static ChatMessageResponse from(ChatMessage message) {
        return from(message, 0, false, null);
    }

    public static ChatMessageResponse from(ChatMessage message, int unreadCount) {
        return from(message, unreadCount, false, null);
    }

    public static ChatMessageResponse from(ChatMessage message, int unreadCount, boolean isMine) {
        return from(message, unreadCount, isMine, null);
    }

    public static ChatMessageResponse from(ChatMessage message, int unreadCount, boolean isMine, String senderProfileImage) {
        return from(message, unreadCount, isMine, message.getSenderNickname(), senderProfileImage);
    }

    public static ChatMessageResponse from(ChatMessage message, int unreadCount, boolean isMine, String senderNickname, String senderProfileImage) {
        return ChatMessageResponse.builder()
                .messageId(message.getMessageId())
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .senderNickname(senderNickname != null ? senderNickname : message.getSenderNickname())
                .senderProfileImage(senderProfileImage)
                .messageType(message.getMessageType())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .unreadCount(unreadCount)
                .isMine(isMine)
                .isDeleted("Y".equals(message.getIsDeleted()))
                .build();
    }
}
