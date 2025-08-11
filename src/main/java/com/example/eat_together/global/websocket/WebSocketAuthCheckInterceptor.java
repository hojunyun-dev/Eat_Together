package com.example.eat_together.global.websocket;

import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static com.example.eat_together.global.util.JwtAuthenticationFilter.AUTHORIZATION_HEADER;
import static com.example.eat_together.global.util.JwtAuthenticationFilter.BEARER_PREFIX;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthCheckInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if(accessor.getCommand() == StompCommand.CONNECT) {
            Principal principal = getPrincipal(accessor);
            accessor.setUser(principal);
        }
        if(accessor.getCommand() == StompCommand.SUBSCRIBE) {
            //구독 권한 체크: 구독 ROOMiD와 동일한가?
        }
        return message;
    }

    protected Principal getPrincipal(StompHeaderAccessor accessor) {
        String token = resolveToken(accessor);
        Principal principal = jwtUtil.getAuthentication(token);

        return principal;
    }

    private String resolveToken(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null)
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        if (!authorization.startsWith(BEARER_PREFIX)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
        return authorization.substring(BEARER_PREFIX.length());
    }
}
