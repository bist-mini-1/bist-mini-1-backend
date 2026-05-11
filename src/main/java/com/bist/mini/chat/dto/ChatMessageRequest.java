package com.bist.mini.chat.dto;

import com.bist.mini.chat.entity.ChatMessage;
import com.bist.mini.chat.entity.ChatMessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 메시지 전송 요청 DTO
 */
@Getter
@Setter
public class ChatMessageRequest {
    
    @NotNull(message = "채팅방 ID는 필수입니다.")
    private Long roomId;

    @NotNull(message = "보내는 사람 ID는 필수입니다.")
    private Long senderId;
    
    @NotNull(message = "메시지 타입은 필수입니다.")
    private ChatMessageType messageType;
    
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    public ChatMessage toEntity() {
        return ChatMessage.builder()
                .roomId(this.roomId)
                .senderId(this.senderId)
                .messageType(this.messageType)
                .content(this.content)
                .build();
    }
}
