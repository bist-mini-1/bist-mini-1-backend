package com.bist.mini.chat.dto;

import com.bist.mini.chat.entity.ChatRoom;
import com.bist.mini.chat.entity.ChatRoomType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 채팅방 응답 DTO
 */
@Getter
@Builder
public class ChatRoomResponse {
    private Long roomId;
    private String roomName;
    private ChatRoomType roomType;
    private LocalDateTime createdAt;
    
    // 마지막 메시지 정보
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    
    // 상대방 정보 (PERSONAL 채팅용)
    private String partnerNickname;
    private String partnerProfileImage;

    public static ChatRoomResponse of(ChatRoom room, String lastMessage, LocalDateTime lastMessageTime, String partnerNickname, String partnerProfileImage) {
        return ChatRoomResponse.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .roomType(room.getRoomType())
                .createdAt(room.getCreatedAt())
                .lastMessage(lastMessage)
                .lastMessageTime(lastMessageTime)
                .partnerNickname(partnerNickname)
                .partnerProfileImage(partnerProfileImage)
                .build();
    }
}
