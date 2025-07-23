package com.example.eat_together.domain.chat.controller;

import com.example.eat_together.domain.chat.chatEnum.ChatResponse;
import com.example.eat_together.domain.chat.dto.ChatGroupDto;
import com.example.eat_together.domain.chat.service.ChatService;
import com.example.eat_together.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chats")
public class ChatController {
    private final ChatService chatService;

    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createChatGroup(@Valid @RequestBody ChatGroupDto chatGroupDto){
        chatService.createChatGroup(chatGroupDto);

        return ResponseEntity.ok(ApiResponse.of(null, ChatResponse.CHATTING_GROUP_CREATED.getMessage()));
    }
}
