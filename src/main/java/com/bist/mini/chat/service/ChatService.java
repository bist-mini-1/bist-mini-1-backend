package com.bist.mini.chat.service;

import com.bist.mini.chat.dao.ChatMessageDao;
import com.bist.mini.chat.dao.ChatRoomDao;
import com.bist.mini.chat.dto.ChatMessageResponse;
import com.bist.mini.chat.dto.ChatRoomResponse;
import com.bist.mini.chat.entity.ChatMessage;
import com.bist.mini.chat.entity.ChatRoom;
import com.bist.mini.chat.entity.ChatRoomMember;
import com.bist.mini.chat.entity.ChatRoomType;
import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 채팅 비즈니스 로직 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomDao chatRoomDao;
    private final ChatMessageDao chatMessageDao;
    private final SimpMessageSendingOperations messagingTemplate;
    private final NotificationService notificationService;

    /**
     * 1:1 채팅방 조회 또는 생성
     */
    @Transactional
    public ChatRoom getOrCreatePersonalRoom(Long memberId1, Long memberId2) {
        if (memberId1.equals(memberId2)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 1. 기존 1:1 채팅방 존재 여부 확인
        Long existingRoomId = chatRoomDao.findPersonalRoom(memberId1, memberId2);
        if (existingRoomId != null) {
            return chatRoomDao.findRoomById(existingRoomId);
        }

        // 2. 존재하지 않으면 생성
        ChatRoom room = ChatRoom.builder()
                .roomType(ChatRoomType.PERSONAL)
                .createdAt(LocalDateTime.now())
                .build();
        chatRoomDao.insertRoom(room);

        // 3. 멤버 추가
        chatRoomDao.insertRoomMember(ChatRoomMember.builder()
                .roomId(room.getRoomId())
                .memberId(memberId1)
                .joinedAt(LocalDateTime.now())
                .build());
        chatRoomDao.insertRoomMember(ChatRoomMember.builder()
                .roomId(room.getRoomId())
                .memberId(memberId2)
                .joinedAt(LocalDateTime.now())
                .build());

        return room;
    }

    /**
     * 회원의 채팅방 목록 조회
     */
    public List<ChatRoomResponse> getRoomList(Long memberId) {
        if (memberId == null) return List.of();
        List<ChatRoom> rooms = chatRoomDao.findRoomsByMemberId(memberId);
        if (rooms == null) return List.of();

        return rooms.stream().map(room -> {
            ChatMessage lastMsg = chatMessageDao.findLastMessage(room.getRoomId());
            String lastContent = (lastMsg != null) ? lastMsg.getContent() : "대화 내용이 없습니다.";
            
            // lastTime이 null인 경우 방 생성 시간을 기본값으로 사용
            LocalDateTime lastTime = (lastMsg != null && lastMsg.getCreatedAt() != null) 
                    ? lastMsg.getCreatedAt() 
                    : (room.getCreatedAt() != null ? room.getCreatedAt() : LocalDateTime.now());

            String partnerNickname = null;
            String partnerProfileImage = null;

            if (room.getRoomType() != null && room.getRoomType() == ChatRoomType.PERSONAL) {
                List<ChatRoomMember> members = chatRoomDao.findMembersByRoomId(room.getRoomId());
                if (members != null) {
                    for (ChatRoomMember m : members) {
                        if (m.getMemberId() != null && !m.getMemberId().equals(memberId)) {
                            partnerNickname = m.getNickname();
                            // profileImage (byte[])는 필요시 Base64 등으로 변환하여 처리 가능
                            break;
                        }
                    }
                }
            }

            return ChatRoomResponse.of(room, lastContent, lastTime, partnerNickname, partnerProfileImage, unreadCount(room.getRoomId(), memberId));
        }).collect(Collectors.toList());
    }

    private int unreadCount(Long roomId, Long memberId) {
        List<ChatRoomMember> members = chatRoomDao.findMembersByRoomId(roomId);
        if (members == null) return 0;
        for (ChatRoomMember rm : members) {
            if (rm.getMemberId() != null && rm.getMemberId().equals(memberId)) {
                return chatMessageDao.countUnreadMessages(roomId, rm.getLastReadAt());
            }
        }
        return 0;
    }

    /**
     * 메시지 전송
     */
    @Transactional
    public ChatMessage sendMessage(ChatMessage message) {
        // 방 참여 여부 확인
        validateRoomAccess(message.getRoomId(), message.getSenderId());

        message.setCreatedAt(LocalDateTime.now());
        chatMessageDao.insertMessage(message);

        // 마지막 읽은 시간 업데이트
        chatRoomDao.updateLastReadAt(message.getRoomId(), message.getSenderId());

        // 수신자들에게 SSE 알림 전송 (안 읽음 카운트 갱신용)
        List<ChatRoomMember> members = chatRoomDao.findMembersByRoomId(message.getRoomId());
        for (ChatRoomMember m : members) {
            if (!m.getMemberId().equals(message.getSenderId())) {
                // notificationService의 범용 send 메서드 활용
                // 데이터는 단순 문자열이나 간단한 객체 전송
                notificationService.send(m.getMemberId(), "new_message", "chat_unread_update");
            }
        }

        return message;
    }

    /**
     * 채팅 내역 조회 (페이징)
     */
    public List<ChatMessage> getMessageHistory(Long roomId, Long memberId, int page, int size) {
        validateRoomAccess(roomId, memberId);

        int offset = (page - 1) * size;
        return chatMessageDao.findMessagesByRoomId(roomId, offset, size);
    }

    /**
     * 읽음 처리
     */
    @Transactional
    public void markAsRead(Long roomId, Long memberId) {
        chatRoomDao.updateLastReadAt(roomId, memberId);
        
        // 읽음 이벤트 전송 (실시간 '1' 제거용)
        Map<String, Object> readEvent = new HashMap<>();
        readEvent.put("messageType", "READ"); // 클라이언트 구분을 위해 messageType 필드 활용
        readEvent.put("roomId", roomId);
        readEvent.put("senderId", memberId); // 누가 읽었는지
        readEvent.put("createdAt", LocalDateTime.now()); // 읽은 시간
        
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, readEvent);
    }

    /**
     * DTO 변환 헬퍼 (Service -> Controller)
     */
    public ChatMessageResponse convertToResponse(ChatMessage message) {
        return ChatMessageResponse.from(message);
    }

    public ChatMessageResponse convertToResponse(ChatMessage message, Long currentMemberId) {
        boolean isMine = message.getSenderId() != null && message.getSenderId().equals(currentMemberId);
        return ChatMessageResponse.from(message, 0, isMine);
    }

    public List<ChatMessageResponse> convertToResponses(List<ChatMessage> messages, Long currentMemberId) {
        if (messages.isEmpty()) return List.of();
        
        Long roomId = messages.get(0).getRoomId();
        List<ChatRoomMember> members = chatRoomDao.findMembersByRoomId(roomId);
        
        return messages.stream().map(message -> {
            int unreadCount = 0;
            if (members != null) {
                for (ChatRoomMember m : members) {
                    if (m.getMemberId() != null && m.getMemberId().equals(message.getSenderId())) continue;
                    
                    // 상대방이 마지막으로 읽은 시간보다 메시지 생성 시간이 뒤면 안 읽음
                    if (m.getLastReadAt() == null || (message.getCreatedAt() != null && m.getLastReadAt().isBefore(message.getCreatedAt()))) {
                        unreadCount++;
                    }
                }
            }
            boolean isMine = message.getSenderId() != null && message.getSenderId().equals(currentMemberId);
            return ChatMessageResponse.from(message, unreadCount, isMine);
        }).collect(Collectors.toList());
    }

    /**
     * 메시지 수정
     */
    @Transactional
    public ChatMessage editMessage(Long messageId, String content, Long memberId) {
        ChatMessage message = chatMessageDao.findMessageById(messageId);
        if (message == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!message.getSenderId().equals(memberId)) {
            throw new CustomException(ErrorCode.CHAT_ACCESS_DENIED);
        }

        chatMessageDao.updateMessage(messageId, content);
        message.setContent(content);

        // 수정 이벤트 전송 (실시간 반영용)
        Map<String, Object> editEvent = new HashMap<>();
        editEvent.put("messageType", "UPDATE");
        editEvent.put("messageId", messageId);
        editEvent.put("roomId", message.getRoomId());
        editEvent.put("content", content);
        
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), editEvent);

        return message;
    }

    /**
     * 메시지 삭제
     */
    @Transactional
    public void deleteMessage(Long messageId, Long memberId) {
        ChatMessage message = chatMessageDao.findMessageById(messageId);
        if (message == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!message.getSenderId().equals(memberId)) {
            throw new CustomException(ErrorCode.CHAT_ACCESS_DENIED);
        }

        chatMessageDao.deleteMessage(messageId);

        // 삭제 이벤트 전송 (실시간 반영용) - 소프트 삭제이므로 UPDATE 타입으로 보냄
        Map<String, Object> deleteEvent = new HashMap<>();
        deleteEvent.put("messageType", "UPDATE");
        deleteEvent.put("messageId", messageId);
        deleteEvent.put("roomId", message.getRoomId());
        deleteEvent.put("isDeleted", true);
        deleteEvent.put("content", "삭제된 메시지입니다.");
        
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), deleteEvent);
    }

    /**
     * 채팅방 접근 권한 검증
     */
    private void validateRoomAccess(Long roomId, Long memberId) {
        List<ChatRoomMember> members = chatRoomDao.findMembersByRoomId(roomId);
        boolean isParticipant = members.stream()
                .anyMatch(m -> m.getMemberId().equals(memberId));

        if (!isParticipant) {
            throw new CustomException(ErrorCode.CHAT_ACCESS_DENIED);
        }
    }
}
