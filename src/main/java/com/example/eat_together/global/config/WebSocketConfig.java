package com.example.eat_together.global.config;

import com.example.eat_together.global.util.JwtUtil;
import com.example.eat_together.global.websocket.ChatMessageHandler;
import com.example.eat_together.global.websocket.WebSocketHandShakeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatMessageHandler chatMessageHandler;
    private final JwtUtil jwtUtil;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        System.out.println("이건 등록된거란 뜻이지");
        registry.addHandler(chatMessageHandler, "/chats/send/**")
                .setAllowedOrigins("*")
                .setHandshakeHandler(new WebSocketHandShakeHandler(jwtUtil));
    }
}
