package com.example.eat_together.global.config;

import com.example.eat_together.global.util.JwtUtil;
//import com.example.eat_together.global.websocket.ChatMessageHandler;
import com.example.eat_together.global.websocket.WebSocketAuthCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //JwtUtil 주입 제거: 인터셉터에서 JWT 처리하므로 여기선 불필요
    private final WebSocketAuthCheckInterceptor webSocketAuthCheckInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //WebSocketHandler 등록
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
/*
        registry.addEndpoint("/chats/send")
                .setAllowedOrigins("*")
                .withSockJS();

 */
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthCheckInterceptor);
    }
}
