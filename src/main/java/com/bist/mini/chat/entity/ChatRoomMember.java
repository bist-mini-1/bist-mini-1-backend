package com.bist.mini.chat.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅방 멤버 엔티티
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMember {
    private Long roomMemberId;
    private Long roomId;
    private Long memberId;
    private LocalDateTime joinedAt;
    private LocalDateTime lastReadAt;

    // 조인을 위한 추가 필드
    private String nickname;
    private String profileImageUrl;
}
