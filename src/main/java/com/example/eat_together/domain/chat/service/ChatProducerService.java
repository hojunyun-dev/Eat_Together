package com.example.eat_together.domain.chat.service;

import com.example.eat_together.domain.chat.dto.ChatMessageRequestDto;
import com.example.eat_together.domain.chat.dto.ChatMessageResponseDto;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatProducerService {
    private static final String TOPIC = "chats";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ChatUtil chatUtil;

    public void send(Long roomId, ChatMessageRequestDto message) {
        try{
            ChatMessageResponseDto chatMessageResponseDto = ChatMessageResponseDto.of(message.getUserId(), roomId, message.getMessage());
            chatUtil.saveMessage(message, message.getUserId(), roomId);
            String jsonMessage = objectMapper.writeValueAsString(chatMessageResponseDto);
            kafkaTemplate.send(TOPIC, String.valueOf(roomId), jsonMessage);
        } catch(Exception e){
            throw new CustomException(ErrorCode.CANT_PRODUCE_MESSAGE);
        }
    }
}
