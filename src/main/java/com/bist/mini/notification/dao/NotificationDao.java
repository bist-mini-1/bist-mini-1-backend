package com.bist.mini.notification.dao;

import com.bist.mini.notification.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationDao {

    /**
     * 알림 생성
     */
    void insert(Notification notification);

    /**
     * 알림 상세 조회
     */
    Notification findById(Long notificationId);

    /**
     * 회원의 알림 목록 조회
     */
    List<Notification> findByReceiverId(Long receiverId);

    /**
     * 알림 읽음 처리
     */
    void markAsRead(@Param("notificationId") Long notificationId);

    /**
     * 회원의 모든 알림 읽음 처리 (선택 기능)
     */
    void markAllAsRead(@Param("receiverId") Long receiverId);

    /**
     * 알림 삭제
     */
    void delete(@Param("notificationId") Long notificationId);

    /**
     * 모든 알림 삭제
     */
    void deleteAll(@Param("receiverId") Long receiverId);

    /**
     * 중복 알림 체크 (팔로우 등에서 사용)
     */
    int countDuplicate(@Param("receiverId") Long receiverId,
            @Param("senderId") Long senderId,
            @Param("type") String type,
            @Param("postId") Long postId);
}
