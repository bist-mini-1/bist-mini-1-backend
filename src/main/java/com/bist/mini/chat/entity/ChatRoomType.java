package com.bist.mini.chat.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 채팅방 타입 (1:1, 그룹)
 */
@Getter
@RequiredArgsConstructor
public enum ChatRoomType {
    PERSONAL("1:1 채팅"),
    GROUP("그룹 채팅");

    private final String description;
}
