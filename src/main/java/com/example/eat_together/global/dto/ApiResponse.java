package com.example.eat_together.global.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {

    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    @Builder
    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> of(T data, String message) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> sccuess(String message) {
        return ApiResponse.<T>builder()
                .message(message)
                .build();
    }
}
