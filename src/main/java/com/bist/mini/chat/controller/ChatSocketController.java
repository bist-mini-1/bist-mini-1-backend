package com.bist.mini.chat.controller;

import com.bist.mini.chat.dto.ChatMessageRequest;
import com.bist.mini.chat.dto.ChatMessageResponse;
import com.bist.mini.chat.entity.ChatMessage;
import com.bist.mini.chat.service.ChatService;
import com.bist.mini.common.annotation.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

/**
 * 실시간 채팅 메시지 처리 컨트롤러 (WebSocket)
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    /**
     * 채팅 메시지 수신 및 브로드캐스팅
     * 클라이언트가 /pub/chat/message로 보낸 메시지를 처리
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessageRequest request, @LoginMember Long senderId) {
        // 1. 메시지 엔티티 변환
        ChatMessage message = ChatMessage.builder()
                .roomId(request.getRoomId())
                .senderId(senderId)
                .messageType(request.getMessageType())
                .content(request.getContent())
                .build();

        // 2. 메시지 저장 및 가공
        ChatMessage savedMessage = chatService.sendMessage(message);
        
        // 실시간 전송 시 프로필 정보 등을 포함한 DTO로 변환
        // (isMine은 프론트엔드에서 senderId 기반으로 다시 판단하므로 임의로 false 처리해도 무관)
        ChatMessageResponse response = chatService.convertToResponse(savedMessage, -1L);

        // 3. 해당 채팅방 구독자들에게 메시지 전송 (/sub/chat/room/{roomId})
        messagingTemplate.convertAndSend("/sub/chat/room/" + response.getRoomId(), response);
    }
}
