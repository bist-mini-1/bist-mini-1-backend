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
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C005", "인증이 필요합니다."),

    // Sample
    SAMPLE_ERROR(HttpStatus.BAD_REQUEST, "S001", "샘플 에러 예시입니다."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CM001", "해당 댓글을 찾을 수 없습니다."),
    COMMENT_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "CM002", "해당 댓글은 이미 삭제되었습니다."),
    COMMENT_REPLY_DEPTH_EXCEEDED(HttpStatus.NOT_FOUND, "CM003", "대댓글에는 답글을 달 수 없습니다. (1단계까지만 허용)"),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CM004", "작성자만 수정/삭제할 수 있습니다."),

    // Post
    POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "P001", "해당 게시글에 대한 권한이 없습니다."),
    
    // Auth
    FORBIDDEN(HttpStatus.FORBIDDEN, "A001", "접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
