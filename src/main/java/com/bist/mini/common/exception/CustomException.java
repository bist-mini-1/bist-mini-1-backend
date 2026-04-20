package com.bist.mini.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 처리 중 발생하는 전용 예외
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
