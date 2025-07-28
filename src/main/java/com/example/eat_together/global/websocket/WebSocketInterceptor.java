package com.example.eat_together.global.websocket;

import com.example.eat_together.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

}