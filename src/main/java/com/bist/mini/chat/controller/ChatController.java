package com.bist.mini.chat.controller;

import com.bist.mini.chat.dto.ChatMessageRequest;
import com.bist.mini.chat.dto.ChatMessageResponse;
import com.bist.mini.chat.dto.ChatRoomResponse;
import com.bist.mini.chat.entity.ChatMessage;
import com.bist.mini.chat.entity.ChatRoom;
import com.bist.mini.chat.service.ChatService;
import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.annotation.LoginMember;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 채팅 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/rooms/personal/{partnerId}")
    public ApiResponse<ChatRoomResponse> getOrCreatePersonalRoom(
            @LoginMember Long memberId,
            @PathVariable("partnerId") Long partnerId) {
        log.info("1:1 채팅방 요청 시작: memberId={}, partnerId={}", memberId, partnerId);
        try {
            ChatRoom room = chatService.getOrCreatePersonalRoom(memberId, partnerId);
            log.info("채팅방 생성/조회 완료: roomId={}", room.getRoomId());
            
            List<ChatRoomResponse> rooms = chatService.getRoomList(memberId);
            ChatRoomResponse response = rooms.stream()
                    .filter(r -> r.getRoomId().equals(room.getRoomId()))
                    .findFirst()
                    .orElse(null);
            
            if (response == null) {
                log.warn("생성된 채팅방을 목록에서 찾을 수 없음: roomId={}", room.getRoomId());
            }
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("채팅방 생성 중 오류 발생: ", e);
            throw e;
        }
    }

    @GetMapping("/rooms")
    public ApiResponse<List<ChatRoomResponse>> getRoomList(@LoginMember Long memberId) {
        log.debug("채팅방 목록 조회 요청: memberId={}", memberId);
        return ApiResponse.success(chatService.getRoomList(memberId));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ApiResponse<List<ChatMessageResponse>> getMessageHistory(
            @LoginMember Long memberId,
            @PathVariable("roomId") Long roomId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.debug("메시지 내역 조회 요청: roomId={}, memberId={}, page={}", roomId, memberId, page);
        List<ChatMessage> history = chatService.getMessageHistory(roomId, memberId, page, size);
        return ApiResponse.success(chatService.convertToResponses(history, memberId));
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ApiResponse<ChatMessageResponse> sendMessage(
            @LoginMember Long memberId,
            @PathVariable("roomId") Long roomId,
            @Valid @RequestBody ChatMessageRequest request) {
        log.debug("메시지 전송 요청: roomId={}, memberId={}", roomId, memberId);
        // DTO에 ID 설정 후 엔티티 변환
        request.setRoomId(roomId);
        request.setSenderId(memberId);
        ChatMessage message = request.toEntity();
        ChatMessage sent = chatService.sendMessage(message);
        return ApiResponse.success(chatService.convertToResponse(sent, memberId));
    }

    @PutMapping("/messages/{messageId}")
    public ApiResponse<ChatMessageResponse> editMessage(
            @LoginMember Long memberId,
            @PathVariable("messageId") Long messageId,
            @RequestBody Map<String, String> body) {
        String content = body.get("content");
        ChatMessage edited = chatService.editMessage(messageId, content, memberId);
        return ApiResponse.success(chatService.convertToResponse(edited, memberId));
    }

    @DeleteMapping("/messages/{messageId}")
    public ApiResponse<Void> deleteMessage(
            @LoginMember Long memberId,
            @PathVariable("messageId") Long messageId) {
        chatService.deleteMessage(messageId, memberId);
        return ApiResponse.success(null);
    }

    @PatchMapping("/rooms/{roomId}/read")
    public ApiResponse<Void> markAsRead(
            @LoginMember Long memberId,
            @PathVariable("roomId") Long roomId) {
        log.debug("채팅 읽음 처리 요청: roomId={}, memberId={}", roomId, memberId);
        chatService.markAsRead(roomId, memberId);
        return ApiResponse.success(null);
    }
}
