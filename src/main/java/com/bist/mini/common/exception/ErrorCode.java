package com.bist.mini.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 전역 에러 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "허용되지 않은 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부 오류가 발생했습니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "해당 엔티티를 찾을 수 없습니다."),

    // Sample
    SAMPLE_ERROR(HttpStatus.BAD_REQUEST, "S001", "샘플 에러 예시입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
