package com.example.eat_together.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("권한 부족 접근: {}", accessDeniedException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpStatus.FORBIDDEN.value());
        errorDetails.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        errorDetails.put("message", "접근 권한이 없습니다."); // 원하는 메시지
        errorDetails.put("path", request.getRequestURI());

        try (OutputStream os = response.getOutputStream()) {
            objectMapper.writeValue(os, errorDetails);
            os.flush();
        }
    }
}