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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
        List<ChatRoom> rooms = chatRoomDao.findRoomsByMemberId(memberId);

        return rooms.stream().map(room -> {
            ChatMessage lastMsg = chatMessageDao.findLastMessage(room.getRoomId());
            String lastContent = (lastMsg != null) ? lastMsg.getContent() : "대화 내용이 없습니다.";
            LocalDateTime lastTime = (lastMsg != null) ? lastMsg.getCreatedAt() : room.getCreatedAt();

            String partnerNickname = null;
            String partnerProfileImage = null;

            if (room.getRoomType() == ChatRoomType.PERSONAL) {
                List<ChatRoomMember> members = chatRoomDao.findMembersByRoomId(room.getRoomId());
                for (ChatRoomMember m : members) {
                    if (!m.getMemberId().equals(memberId)) {
                        partnerNickname = m.getNickname();
                        partnerProfileImage = m.getProfileImage();
                        break;
                    }
                }
            }

            return ChatRoomResponse.of(room, lastContent, lastTime, partnerNickname, partnerProfileImage);
        }).collect(Collectors.toList());
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
    }

    /**
     * DTO 변환 헬퍼 (Service -> Controller)
     */
    public ChatMessageResponse convertToResponse(ChatMessage message) {
        return ChatMessageResponse.from(message);
    }

    public List<ChatMessageResponse> convertToResponses(List<ChatMessage> messages) {
        return messages.stream().map(ChatMessageResponse::from).collect(Collectors.toList());
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
