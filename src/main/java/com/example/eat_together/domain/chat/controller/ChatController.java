package com.example.eat_together.domain.chat.controller;

import com.example.eat_together.domain.chat.dto.ChatMessageRequestDto;
import com.example.eat_together.domain.chat.service.ChatProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatProducerService chatProducerService;

    @MessageMapping("/roomId/{roomId}")
    public void send(ChatMessageRequestDto chatMessageRequestDto, @DestinationVariable Long roomId, Principal principal) {
        System.out.println("===================");
        System.out.println(principal.getName());
        chatProducerService.send(roomId, chatMessageRequestDto);
    }

}
