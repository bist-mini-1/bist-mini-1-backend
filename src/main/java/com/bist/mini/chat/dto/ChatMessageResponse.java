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
    private ChatMessageType messageType;
    private String content;
    private LocalDateTime createdAt;
    private int unreadCount; // 안 읽은 인원 수 (1:1에서는 0 또는 1)
    private boolean isMine;

    public static ChatMessageResponse from(ChatMessage message) {
        return from(message, 0, false);
    }

    public static ChatMessageResponse from(ChatMessage message, int unreadCount) {
        return from(message, unreadCount, false);
    }

    public static ChatMessageResponse from(ChatMessage message, int unreadCount, boolean isMine) {
        return ChatMessageResponse.builder()
                .messageId(message.getMessageId())
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .senderNickname(message.getSenderNickname())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .unreadCount(unreadCount)
                .isMine(isMine)
                .build();
    }
}
