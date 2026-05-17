package com.bist.mini.chat.controller;

import com.bist.mini.chat.dto.ChatMessageRequest;
import com.bist.mini.chat.dto.ChatMessageResponse;
import com.bist.mini.chat.dto.ChatRoomResponse;
import com.bist.mini.chat.entity.ChatMessage;
import com.bist.mini.chat.entity.ChatRoom;
import com.bist.mini.chat.service.ChatService;
import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Chat", description = "채팅 관리 API")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "1:1 채팅방 조회/생성", description = "상대방과의 1:1 채팅방을 조회하거나 없으면 새로 생성합니다.")
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

    @Operation(summary = "채팅방 목록 조회", description = "로그인한 사용자가 참여 중인 채팅방 목록을 조회합니다.")
    @GetMapping("/rooms")
    public ApiResponse<List<ChatRoomResponse>> getRoomList(@LoginMember Long memberId) {
        log.debug("채팅방 목록 조회 요청: memberId={}", memberId);
        return ApiResponse.success(chatService.getRoomList(memberId));
    }

    @Operation(summary = "메시지 내역 조회", description = "특정 채팅방의 이전 대화 내역을 페이징하여 조회합니다.")
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

    @Operation(summary = "메시지 전송 (REST)", description = "채팅 메시지를 전송합니다. (실시간은 WebSocket 권장)")
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

    @Operation(summary = "메시지 수정", description = "내가 보낸 메시지를 수정합니다.")
    @PutMapping("/messages/{messageId}")
    public ApiResponse<ChatMessageResponse> editMessage(
            @LoginMember Long memberId,
            @PathVariable("messageId") Long messageId,
            @RequestBody Map<String, String> body) {
        String content = body.get("content");
        ChatMessage edited = chatService.editMessage(messageId, content, memberId);
        return ApiResponse.success(chatService.convertToResponse(edited, memberId));
    }

    @Operation(summary = "메시지 삭제", description = "내가 보낸 메시지를 삭제합니다.")
    @DeleteMapping("/messages/{messageId}")
    public ApiResponse<Void> deleteMessage(
            @LoginMember Long memberId,
            @PathVariable("messageId") Long messageId) {
        chatService.deleteMessage(messageId, memberId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "읽음 처리", description = "채팅방의 메시지를 모두 읽음 처리합니다.")
    @PatchMapping("/rooms/{roomId}/read")
    public ApiResponse<Void> markAsRead(
            @LoginMember Long memberId,
            @PathVariable("roomId") Long roomId) {
        log.debug("채팅 읽음 처리 요청: roomId={}, memberId={}", roomId, memberId);
        chatService.markAsRead(roomId, memberId);
        return ApiResponse.success(null);
    }
}
