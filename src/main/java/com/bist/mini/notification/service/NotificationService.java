package com.bist.mini.notification.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.notification.dao.NotificationDao;
import com.bist.mini.notification.entity.Notification;
import com.bist.mini.notification.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationDao notificationDao;
    
    // 메시지 만료 시간 (1시간)
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    
    // 사용자별 SSE 연결 관리
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 회원의 알림 목록 조회
     */
    public List<Notification> getNotifications(Long memberId) {
        return notificationDao.findByReceiverId(memberId);
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void markAsRead(Long notificationId, Long memberId) {
        Notification notification = notificationDao.findById(notificationId);
        if (notification == null) {
            throw new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!notification.getReceiverId().equals(memberId)) {
            throw new CustomException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
        }
        notificationDao.markAsRead(notificationId);
    }

    /**
     * 알림 생성 (댓글, 좋아요, 팔로우 시 호출)
     */
    @Transactional
    public void createNotification(Long receiverId, Long senderId, Long postId, Long commentId, NotificationType type, String message) {
        // 본인이 본인 글에 액션을 취한 경우 알림 제외
        if (receiverId.equals(senderId)) {
            return;
        }

        // 중복 알림 방지 (팔로우 등, 읽지 않은 동일 타입 알림이 있으면 생성 안함)
        if (type == NotificationType.FOLLOW) {
            int count = notificationDao.countDuplicate(receiverId, senderId, type.name(), null);
            if (count > 0) return;
        }

        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .postId(postId)
                .commentId(commentId)
                .type(type)
                .message(message)
                .build();

        notificationDao.insert(notification);

        // 실시간 SSE 알림 전송
        sendToClient(receiverId, notification, "알림이 도착했습니다.");
    }

    /**
     * SSE 연결 생성
     */
    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.put(memberId, emitter);
        System.out.println("SSE: Emitter created for member " + memberId + ". Current count: " + emitters.size());

        // 연결 종료/타임아웃 시 맵에서 삭제
        emitter.onCompletion(() -> {
            System.out.println("SSE: Emitter completed for member " + memberId);
            emitters.remove(memberId);
        });
        emitter.onTimeout(() -> {
            System.out.println("SSE: Emitter timeout for member " + memberId);
            emitters.remove(memberId);
        });
        emitter.onError((e) -> {
            System.out.println("SSE: Emitter error for member " + memberId + ": " + e.getMessage());
            emitters.remove(memberId);
        });

        // 503 에러 방지를 위한 더미 데이터 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
            System.out.println("SSE: Initial connect event sent to member " + memberId);
        } catch (IOException e) {
            System.err.println("SSE: Failed to send initial connect event to member " + memberId);
            emitters.remove(memberId);
        }

        return emitter;
    }

    /**
     * 클라이언트에게 데이터 전송
     */
    public void send(Long memberId, Object data, String eventName) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException e) {
                emitters.remove(memberId);
            }
        }
    }

    /**
     * 클라이언트에게 데이터 전송
     */
    private void sendToClient(Long memberId, Object data, String comment) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(memberId))
                        .name("notification")
                        .data(data)
                        .comment(comment));
            } catch (IOException e) {
                emitters.remove(memberId);
            }
        }
    }
}
