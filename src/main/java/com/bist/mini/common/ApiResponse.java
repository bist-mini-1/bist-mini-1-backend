package com.bist.mini.common;

import lombok.Builder;
import lombok.Getter;

/**
 * 프로젝트 전역 API 공통 응답 규격
 */
@Getter
@Builder
public class ApiResponse<T> {

    private String status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    public static ApiResponse<Void> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
                .status("fail")
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .build();
    }
}
