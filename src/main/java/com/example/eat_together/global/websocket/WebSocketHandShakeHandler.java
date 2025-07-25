package com.example.eat_together.global.websocket;

import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
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
        // 1. Request Header에서 JWT 토큰 추출
        String jwt = resolveToken(request);

        // 2. 토큰 유효성 검사 및 인증 처리
        if (StringUtils.hasText(jwt) && jwtUtil.isValidToken(jwt)) {
            // SecurityContextHolder에 이미 인증 정보가 없는 경우에만 처리
            // (이미 인증된 요청이거나 다른 필터에서 처리했을 경우 중복 방지)
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    // JwtUtil을 통해 토큰에서 Authentication 객체 생성
                    Authentication authentication = jwtUtil.getAuthentication(jwt);

                    // SecurityContextHolder에 Authentication 객체 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("JWT 인증 성공: 사용자 ID - {}", authentication.getName());
                } catch (Exception e) {
                    // 토큰 유효성 검사는 isValidToken에서 일차적으로 처리되지만,
                    // getAuthentication 내부에서 발생할 수 있는 추가적인 예외 처리 (예: 클레임 파싱 오류 등)
                    log.error("JWT 인증 처리 중 오류 발생: {}", e.getMessage());
                    // 이 경우, SecurityContext에 인증 정보를 설정하지 않고 다음 필터로 넘깁니다.
                    // 클라이언트에게는 SecurityConfig의 예외 핸들러를 통해 403 Forbidden 등이 반환될 수 있습니다.
                }
            }
        } else {
            // 토큰이 없거나 유효하지 않은 경우 (로그인 필요 또는 잘못된 토큰)
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
        if(httpHeader == null)
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        String bearerToken = httpHeader.get(0);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
