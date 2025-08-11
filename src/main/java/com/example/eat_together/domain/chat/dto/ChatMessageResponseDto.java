package com.example.eat_together.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ChatMessageResponseDto {
    private Long userId;
    private Long roomId;
    private String message;

    public static ChatMessageResponseDto of(Long userId, Long roomId, String message) {
        ChatMessageResponseDto chatMessageResponseDto = new ChatMessageResponseDto();
        chatMessageResponseDto.userId = userId;
        chatMessageResponseDto.roomId = roomId;
        chatMessageResponseDto.message = message;

        return chatMessageResponseDto;
    }

}
