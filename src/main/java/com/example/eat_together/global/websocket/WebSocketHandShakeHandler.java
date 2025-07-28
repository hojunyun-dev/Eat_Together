package com.example.eat_together.global.websocket;

import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static com.example.eat_together.global.util.JwtAuthenticationFilter.AUTHORIZATION_HEADER;
import static com.example.eat_together.global.util.JwtAuthenticationFilter.BEARER_PREFIX;

@Slf4j
@RequiredArgsConstructor
public class WebSocketHandShakeHandler extends DefaultHandshakeHandler {

    private final JwtUtil jwtUtil;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {

        String jwt = resolveToken(request);

        // 2. 토큰 유효성 검사 및 인증 처리
        if (StringUtils.hasText(jwt) && jwtUtil.isValidToken(jwt)) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    Authentication authentication = jwtUtil.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("JWT 인증 성공: 사용자 ID - {}", authentication.getName());
                } catch (Exception e) {
                    log.error("JWT 인증 처리 중 오류 발생: {}", e.getMessage());
                }
            }
        } else {
            if (StringUtils.hasText(jwt)) {
                log.warn("유효하지 않은 JWT 토큰 또는 토큰 없음: {}", jwt);
            } else {
                log.debug("Authorization 헤더에 JWT 토큰이 없습니다.");
            }
        }
        Principal principal = jwtUtil.getAuthentication(jwt);

        return principal;
    }

    private String resolveToken(ServerHttpRequest request) {
        List<String> httpHeader = request.getHeaders().get(AUTHORIZATION_HEADER);
        if (httpHeader == null)
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        String bearerToken = httpHeader.get(0);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
