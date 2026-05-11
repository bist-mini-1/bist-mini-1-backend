package com.bist.mini.chat.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅 메시지 엔티티
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long messageId;
    private Long roomId;
    private Long senderId;
    private ChatMessageType messageType; // TEXT, IMAGE, FILE
    private String content;
    private LocalDateTime createdAt;

    // 조인을 위한 추가 필드
    private String senderNickname;
}
