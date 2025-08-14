package com.example.eat_together.global.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class LoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();
        try {
            MDC.put("method", req.getMethod());
            MDC.put("path", req.getRequestURI());
            MDC.put("query", req.getQueryString());
            chain.doFilter(req, res);
        } finally {
            MDC.put("status", String.valueOf(res.getStatus()));
            MDC.put("duration_ms", String.valueOf(System.currentTimeMillis() - start));
            org.slf4j.LoggerFactory.getLogger("HTTP").info("request");
            MDC.clear();
        }
    }
}