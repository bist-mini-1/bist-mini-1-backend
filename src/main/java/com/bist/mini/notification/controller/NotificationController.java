package com.bist.mini.notification.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.jwt.JwtProvider;
import com.bist.mini.notification.dto.NotificationResponseDto;
import com.bist.mini.notification.service.NotificationService;
import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "알림 구독 (SSE)", description = "실시간 알림을 받기 위해 SSE 연결을 맺습니다.")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("SSE subscription request received. Token present: {}", token != null);
        
        try {
            if (token == null) {
                log.warn("SSE subscription request missing Authorization header");
                return ResponseEntity.status(401).build();
            }
            
            Long memberId = jwtProvider.getMemberIdFromToken(token);
            log.info("SSE subscription request for member: {}", memberId);
            
            return ResponseEntity.ok(notificationService.subscribe(memberId));
        } catch (CustomException e) {
            log.error("SSE subscription custom error: {}", e.getErrorCode());
            return ResponseEntity.status(e.getErrorCode().getStatus()).build();
        } catch (Exception e) {
            log.error("SSE subscription unexpected error", e);
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "내 알림 목록 조회", description = "로그인한 사용자의 알림 목록을 최신순으로 조회합니다.")
    @GetMapping
    public ApiResponse<List<NotificationResponseDto>> getNotifications(
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        log.info("Fetching notifications for member: {}", memberId);
        List<NotificationResponseDto> notifications = notificationService.getNotifications(memberId)
                .stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
        log.info("Found {} notifications for member: {}", notifications.size(), memberId);
        return ApiResponse.success(notifications);
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        notificationService.markAsRead(id, memberId);
        return ApiResponse.success(null);
    }
}
