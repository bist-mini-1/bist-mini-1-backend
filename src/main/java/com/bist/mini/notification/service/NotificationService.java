package com.bist.mini.notification.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.notification.dao.NotificationDao;
import com.bist.mini.notification.entity.Notification;
import com.bist.mini.notification.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationDao notificationDao;

    // 메시지 만료 시간 (1시간)
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    // 사용자별 SSE 연결 관리 (다중 연결 지원을 위해 List 사용)
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * 회원의 알림 목록 조회
     */
    public List<Notification> getNotifications(Long memberId) {
        validateMember(memberId);
        return notificationDao.findByReceiverId(memberId);
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void markAsRead(Long notificationId, Long memberId) {
        validateMember(memberId);
        if (notificationId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

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
     * 모든 알림 읽음 처리
     */
    @Transactional
    public void markAllAsRead(Long memberId) {
        validateMember(memberId);
        log.info("Marking all notifications as read for member: {}", memberId);
        notificationDao.markAllAsRead(memberId);
    }

    /**
     * 알림 삭제
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long memberId) {
        validateMember(memberId);
        if (notificationId == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Notification notification = notificationDao.findById(notificationId);
        if (notification == null) {
            throw new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!notification.getReceiverId().equals(memberId)) {
            throw new CustomException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
        }
        notificationDao.delete(notificationId);
    }

    /**
     * 모든 알림 삭제
     */
    @Transactional
    public void deleteAllNotifications(Long memberId) {
        validateMember(memberId);
        log.info("Deleting all notifications for member: {}", memberId);
        notificationDao.deleteAll(memberId);
    }

    /**
     * 회원 유효성 검사 (Internal)
     */
    private void validateMember(Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    /**
     * 알림 생성 (댓글, 좋아요, 팔로우 시 호출)
     */
    @Transactional
    public void createNotification(Long receiverId, Long senderId, Long postId, Long commentId, NotificationType type,
            String message) {
        if (receiverId == null || type == null) {
            log.warn("Notification creation failed: receiverId or type is null");
            return;
        }

        log.info("Creating notification: type={}, receiver={}, sender={}, message={}", type, receiverId, senderId,
                message);

        // 본인이 본인 글에 액션을 취한 경우 알림 제외
        if (receiverId.equals(senderId)) {
            log.info("Skipping notification: self-action by member {}", senderId);
            return;
        }

        // 중복 알림 방지 (팔로우 등, 읽지 않은 동일 타입 알림이 있으면 생성 안함)
        if (type == NotificationType.FOLLOW) {
            int count = notificationDao.countDuplicate(receiverId, senderId, type.name(), null);
            if (count > 0) {
                log.info("Skipping notification: duplicate follow notification");
                return;
            }
        }

        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .postId(postId)
                .commentId(commentId)
                .type(type)
                .message(message != null ? message : "")
                .isRead("N")
                .createdAt(java.time.LocalDateTime.now())
                .build();

        notificationDao.insert(notification);
        log.info("Notification saved to DB: id={}", notification.getNotificationId());

        // 실시간 SSE 알림 전송
        sendToClient(receiverId, notification, "알림이 도착했습니다.");
    }

    /**
     * SSE 연결 생성
     */
    public SseEmitter subscribe(Long memberId) {
        if (memberId == null)
            return null;

        final Long finalMemberId = Objects.requireNonNull(memberId);
        SseEmitter emitter = new SseEmitter(Objects.requireNonNull(DEFAULT_TIMEOUT));

        emitters.computeIfAbsent(finalMemberId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        log.info("SSE: Emitter created for member {}. Total emitters for member: {}", finalMemberId,
                emitters.get(finalMemberId).size());

        // 연결 종료/타임아웃 시 맵에서 삭제
        emitter.onCompletion(() -> {
            log.info("SSE: Emitter completed for member {}", finalMemberId);
            removeEmitter(finalMemberId, emitter);
        });
        emitter.onTimeout(() -> {
            log.info("SSE: Emitter timeout for member {}", finalMemberId);
            removeEmitter(finalMemberId, emitter);
        });
        emitter.onError((e) -> {
            log.error("SSE: Emitter error for member {}: {}", finalMemberId, e.getMessage());
            removeEmitter(finalMemberId, emitter);
        });

        // 503 에러 방지를 위한 더미 데이터 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
            log.info("SSE: Initial connect event sent to member {}", finalMemberId);
        } catch (IOException e) {
            log.error("SSE: Failed to send initial connect event to member {}", finalMemberId);
            emitters.remove(finalMemberId);
        }

        return emitter;
    }

    /**
     * 클라이언트에게 데이터 전송 (커스텀 이벤트)
     */
    public void send(Long memberId, Object data, String eventName) {
        if (memberId == null || data == null || eventName == null)
            return;

        final Long finalMemberId = Objects.requireNonNull(memberId);
        final String finalEventName = Objects.requireNonNull(eventName);
        final Object finalData = Objects.requireNonNull(data);

        log.info("SSE: Sending event {} to member {}", finalEventName, finalMemberId);
        List<SseEmitter> memberEmitters = emitters.get(finalMemberId);
        if (memberEmitters != null) {
            for (SseEmitter emitter : memberEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name(finalEventName)
                            .data(finalData));
                } catch (IOException e) {
                    log.error("SSE: Failed to send event to member {}, removing one emitter", finalMemberId);
                    removeEmitter(finalMemberId, emitter);
                }
            }
        }
    }

    private void removeEmitter(Long memberId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(memberId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) {
                emitters.remove(memberId);
            }
        }
    }

    /**
     * 클라이언트에게 알림 전송 (기본 notification 이벤트)
     */
    private void sendToClient(Long memberId, Object data, String comment) {
        if (memberId == null || data == null)
            return;

        final Long finalMemberId = Objects.requireNonNull(memberId);
        final Object finalData = Objects.requireNonNull(data);
        final String finalComment = (comment != null) ? comment : "";

        log.info("SSE: Sending notification to member {}", finalMemberId);
        List<SseEmitter> memberEmitters = emitters.get(finalMemberId);
        if (memberEmitters != null) {
            for (SseEmitter emitter : memberEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .id(Objects.requireNonNull(String.valueOf(finalMemberId)))
                            .name("notification")
                            .data(finalData)
                            .comment(finalComment));
                } catch (IOException e) {
                    log.error("SSE: Failed to send notification to member {}, removing one emitter", finalMemberId);
                    removeEmitter(finalMemberId, emitter);
                }
            }
        }
    }
}
