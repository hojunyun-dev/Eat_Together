package com.example.eat_together.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


/*
*
* 유효하지 않은 토큰값이 들어 올 경우,
* error 메세지 출력 및 상태 코드 전달
*
* */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.error("인증되지 않은 접근: {}", authException.getMessage());

        Throwable cause = authException.getCause();
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String errorMessage;
        String errorCode = "AUTHENTICATION_FAILED";

        if (cause instanceof ResponseStatusException) {
            // JwtUtil에서 던진 ResponseStatusException인 경우
            ResponseStatusException rse = (ResponseStatusException) cause;
            status = (HttpStatus) rse.getStatusCode();
            errorMessage = rse.getReason();

            if (errorMessage.contains("만료된 토큰")) {
                errorCode = "TOKEN_EXPIRED";
            } else if (errorMessage.contains("잘못된 JWT 서명")) {
                errorCode = "INVALID_TOKEN_SIGNATURE";
            } else if (errorMessage.contains("지원되지 않는 JWT 토큰")) {
                errorCode = "UNSUPPORTED_TOKEN";
            } else if (errorMessage.contains("JWT 토큰이 잘못되었습니다.")) {
                errorCode = "INVALID_TOKEN_FORMAT";
            } else if (errorMessage.contains("잘못된 사용자 ID 형식")) {
                errorCode = "INVALID_USER_ID_FORMAT";
            }
            log.warn("JWT 관련 상세 오류: HTTP {} - {}", status, errorMessage);

        } else {
            errorMessage = "유효한 인증 토큰을 제공해주세요.";
            errorCode = "AUTHENTICATION_REQUIRED";
            log.warn("일반 인증 오류: {}", authException.getMessage());
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", status.value());
        errorDetails.put("error", errorCode);
        errorDetails.put("message", errorMessage);
        errorDetails.put("path", request.getRequestURI());

        try (OutputStream os = response.getOutputStream()) {
            objectMapper.writeValue(os, errorDetails);
            os.flush();
        }
    }
}