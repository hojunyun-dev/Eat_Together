package com.example.eat_together.domain.chat.service;

import com.example.eat_together.domain.chat.dto.ChatMessageResponseDto;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatConsumerService {
    private static final String TOPIC = "chats";
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = TOPIC)
    public void listen(String message) {
        try {
            ChatMessageResponseDto chatMessageResponseDto = objectMapper.readValue(message, ChatMessageResponseDto.class);
            simpMessageSendingOperations.convertAndSend("/sub/roomId/" + chatMessageResponseDto.getRoomId(), chatMessageResponseDto);
        } catch (Exception e){
            throw new CustomException(ErrorCode.CANT_CONSUME_MESSAGE);
        }
    }
}
