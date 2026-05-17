package com.bist.mini.chat.dao;

import com.bist.mini.chat.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 채팅 메시지 데이터 접근 객체
 */
@Mapper
public interface ChatMessageDao {
    // 메시지 저장
    void insertMessage(ChatMessage chatMessage);

    // 채팅방의 메시지 내역 조회 (페이징)
    List<ChatMessage> findMessagesByRoomId(@Param("roomId") Long roomId, 
                                          @Param("offset") int offset, 
                                          @Param("limit") int limit);

    // 채팅방의 마지막 메시지 조회
    ChatMessage findLastMessage(Long roomId);

    // 안 읽은 메시지 수 조회
    int countUnreadMessages(@Param("roomId") Long roomId, @Param("lastReadAt") java.time.LocalDateTime lastReadAt);

    // 메시지 상세 조회
    ChatMessage findMessageById(Long messageId);

    // 메시지 수정
    void updateMessage(@Param("messageId") Long messageId, @Param("content") String content);

    // 메시지 삭제
    void deleteMessage(Long messageId);
}
