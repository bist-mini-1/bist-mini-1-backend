package com.bist.mini.common.exception;

import com.bist.mini.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리기 - 모든 예외를 ApiResponse 규격으로 응답
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 프로젝트 전용 비즈니스 예외 처리
     */
    /**
     * 프로젝트 전용 비즈니스 예외 처리
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.error("handleCustomException: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        // Exception에 직접 입력한 상세 메시지가 있으면 사용, 없으면 에러 코드의 기본 메시지 사용
        String message = (e.getMessage() != null && !e.getMessage().isBlank()) ? e.getMessage() : errorCode.getMessage();
        
        return ResponseEntity
                .status(errorCode.getStatus().value())
                .body(ApiResponse.error(message));
    }

    /**
     * @Valid 검증 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException");
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus().value())
                .body(ApiResponse.fail(message));
    }

    /**
     * 그 외 예상치 못한 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("handleException", e);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value())
                .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
