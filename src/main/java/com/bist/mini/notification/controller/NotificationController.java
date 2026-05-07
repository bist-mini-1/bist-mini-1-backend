package com.bist.mini.notification.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.jwt.JwtProvider;
import com.bist.mini.notification.dto.NotificationResponseDto;
import com.bist.mini.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "내 알림 목록 조회", description = "로그인한 사용자의 알림 목록을 최신순으로 조회합니다.")
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
