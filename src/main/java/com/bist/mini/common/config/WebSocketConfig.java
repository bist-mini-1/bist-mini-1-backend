package com.bist.mini.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket(STOMP) 설정
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // 메시지 브로커 설정
        // /sub로 시작하는 주소로 구독(Subscribe) 신청
        config.enableSimpleBroker("/sub");
        // /pub로 시작하는 주소로 메시지 발행(Publish) 신청
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // 엔드포인트 설정
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*") // CORS 허용 (실제 운영 시에는 특정 도메인만 허용 권장)
                .withSockJS(); // SockJS 지원
    }
}
