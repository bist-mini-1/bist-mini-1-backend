package com.bist.mini.chat.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 메시지 타입 (텍스트, 이미지, 파일)
 */
@Getter
@RequiredArgsConstructor
public enum ChatMessageType {
    TEXT("텍스트"),
    IMAGE("이미지"),
    FILE("파일");

    private final String description;
}
