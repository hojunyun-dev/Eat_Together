package com.example.eat_together.global.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private static final List<String> PERMIT_ALL_PATHS = Arrays.asList(
            "/auth/signup",
            "/auth/login",
            "/social/**",
            "/users/reissue",
            "/users/redis",
            "/favicon.ico"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        log.info("요청 URI: {}", requestUri); // 요청 URI 로그 추가

        // 1. permitAll() 경로에 해당하는 요청인지 확인합니다.
        // 해당 경로일 경우 JWT 검증을 건너뛰고 다음 필터로 넘깁니다.
        for (String path : PERMIT_ALL_PATHS) {
            if (antPathMatcher.match(path, requestUri)) {
                log.info("PermitAll 경로에 대한 요청: {}. JWT 필터 건너뜀.", requestUri);
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 2. PermitAll 경로가 아닐 경우에만 JWT 토큰 검증 로직을 수행합니다.
        String jwt = resolveToken(request);

        if (StringUtils.hasText(jwt) && jwtUtil.isValidToken(jwt)) {
            // SecurityContextHolder에 이미 인증 정보가 없는 경우에만 처리
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

        // 다음 필터로 요청과 응답 전달
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 헤더에서 JWT 토큰을 추출하는 메서드
     * Authorization: Bearer <token> 형식에서 <token> 부분만 반환
     * @param request HttpServletRequest 객체
     * @return 추출된 JWT 토큰 문자열 (없거나 형식이 맞지 않으면 null 반환)
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}