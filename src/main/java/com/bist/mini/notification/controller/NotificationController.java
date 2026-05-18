package com.bist.mini.notification.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.jwt.JwtProvider;
import com.bist.mini.notification.dto.NotificationResponseDto;
import com.bist.mini.notification.service.NotificationService;
import com.bist.mini.common.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtProvider jwtProvider;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("SSE subscription request received. Token present: {}", token != null);

        if (token == null || token.isBlank()) {
            log.warn("SSE subscription request missing Authorization header");
            return ResponseEntity.status(401).build();
        }

        try {
            Long memberId = jwtProvider.getMemberIdFromToken(token);
            if (memberId == null) {
                log.warn("Failed to extract memberId from token for SSE subscription");
                return ResponseEntity.status(401).build();
            }
            log.info("SSE subscription request for member: {}", memberId);
            return ResponseEntity.ok(notificationService.subscribe(memberId));
        } catch (CustomException e) {
            log.error("SSE subscription custom error: {}", e.getErrorCode());
            return ResponseEntity.status(e.getErrorCode().getStatus().value()).build();
        } catch (Exception e) {
            log.error("SSE subscription unexpected error", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping
    public ApiResponse<List<NotificationResponseDto>> getNotifications(
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        List<NotificationResponseDto> notifications = notificationService.getNotifications(memberId)
                .stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
        return ApiResponse.success(notifications);
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        notificationService.markAsRead(id, memberId);
        return ApiResponse.success();
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        notificationService.markAllAsRead(memberId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        notificationService.deleteNotification(id, memberId);
        return ApiResponse.success();
    }

    @DeleteMapping("/all")
    public ApiResponse<Void> deleteAllNotifications(
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        notificationService.deleteAllNotifications(memberId);
        return ApiResponse.success();
    }
}
