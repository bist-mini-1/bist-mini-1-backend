package com.bist.mini.chat.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅방 엔티티
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    private Long roomId;
    private String roomName;
    private ChatRoomType roomType; // PERSONAL, GROUP
    private LocalDateTime createdAt;
}
