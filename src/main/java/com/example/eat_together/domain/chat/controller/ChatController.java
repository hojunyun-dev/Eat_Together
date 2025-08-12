package com.example.eat_together.domain.chat.controller;

import com.example.eat_together.domain.chat.dto.ChatMessageRequestDto;
import com.example.eat_together.domain.chat.service.ChatProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatProducerService chatProducerService;

    @MessageMapping("/roomId/{roomId}")
    public void send(ChatMessageRequestDto chatMessageRequestDto, @DestinationVariable Long roomId) {
        chatProducerService.send(roomId, chatMessageRequestDto);
    }

}
