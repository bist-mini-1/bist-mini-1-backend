package com.bist.mini.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeleteStatus {
    Y("삭제됨"),
    N("삭제되지 않음");

    private final String description;
}
