package com.example.eat_together.domain.chat.controller;

import com.example.eat_together.domain.chat.chatEnum.ChatResponse;
import com.example.eat_together.domain.chat.dto.ChatGroupDto;
import com.example.eat_together.domain.chat.service.ChatService;
import com.example.eat_together.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chats")
public class ChatController {
    private final ChatService chatService;

    //채팅방 생성
    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createChatGroup(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ChatGroupDto chatGroupDto){
        chatService.createChatGroup(Long.valueOf(userDetails.getUsername()), chatGroupDto);

        return ResponseEntity.ok(ApiResponse.of(null, ChatResponse.CHATTING_GROUP_CREATED.getMessage()));
    }

    //채팅방 참여 및 입장
    @PostMapping("/{roomId}/join")
    public ResponseEntity<ApiResponse<Void>> enterChatRoom(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long roomId){
        chatService.enterChatRoom(Long.valueOf(userDetails.getUsername()), roomId);

        return ResponseEntity.ok(ApiResponse.of(null, ChatResponse.CHATTING_GROUP_PARTICIPATED.getMessage()));
    }

    //채팅방 조회
}
