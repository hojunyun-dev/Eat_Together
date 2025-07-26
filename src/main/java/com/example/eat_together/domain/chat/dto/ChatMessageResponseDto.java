package com.example.eat_together.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ChatMessageResponseDto {
    private Long roomId;
    private Long userId;
    private String message;
    private LocalDateTime updateAt;

    public static ChatMessageResponseDto of(Long roomId, Long userId, String message, LocalDateTime updateAt) {
        ChatMessageResponseDto chatMessageResponseDto = new ChatMessageResponseDto();
        chatMessageResponseDto.roomId = roomId;
        chatMessageResponseDto.userId = userId;
        chatMessageResponseDto.message = message;
        chatMessageResponseDto.updateAt = updateAt;

        return chatMessageResponseDto;
    }

}
