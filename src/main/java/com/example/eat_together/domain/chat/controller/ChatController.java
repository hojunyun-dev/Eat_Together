package com.example.eat_together.domain.chat.controller;

import com.example.eat_together.domain.chat.chatEnum.ChatResponse;
import com.example.eat_together.domain.chat.dto.ChatGroupDto;
import com.example.eat_together.domain.chat.dto.ChatMessageResponseDto;
import com.example.eat_together.domain.chat.dto.ChatRoomDto;
import com.example.eat_together.domain.chat.service.ChatService;
import com.example.eat_together.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Secured("USER")
@RequiredArgsConstructor
@RestController
@RequestMapping("/chats")
public class ChatController {
    private final ChatService chatService;

    //채팅방 생성
    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createChatGroup(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ChatGroupDto chatGroupDto) {
        chatService.createChatGroup(Long.valueOf(userDetails.getUsername()), chatGroupDto);

        return ResponseEntity.ok(ApiResponse.of(null, ChatResponse.CREATE_CHAT_ROOM.getMessage()));
    }

    //채팅방 참여 및 입장
    @PostMapping("/{roomId}/enter")
    public ResponseEntity<ApiResponse<Void>> enterChatRoom(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long roomId) {
        chatService.enterChatRoom(Long.valueOf(userDetails.getUsername()), roomId);

        return ResponseEntity.ok(ApiResponse.of(null, ChatResponse.PARTICIPATE_CHAT_ROOM.getMessage()));
    }

    //채팅방 조회
    @GetMapping()
    public ResponseEntity<ApiResponse<List<ChatRoomDto>>> getChatRoomList() {
        List<ChatRoomDto> chatRoomDtoList = chatService.getChatRoomList();

        return ResponseEntity.ok(ApiResponse.of(chatRoomDtoList, ChatResponse.READ_CHAT_ROOM_LIST.getMessage()));
    }

    //기존 채팅 내역 조회
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<List<ChatMessageResponseDto>>> getChatMessageList(@PathVariable Long roomId) {
        List<ChatMessageResponseDto> chatMessageList = chatService.getChatMessageList(roomId);

        return ResponseEntity.ok(ApiResponse.of(chatMessageList, ChatResponse.READ_CHAT_MESSAGE_LIST.getMessage()));
    }

    //채팅방 퇴장
    @Transactional
    @DeleteMapping("/{roomId}/quit")
    public ResponseEntity<ApiResponse<Void>> quitChatRoom(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long roomId) {
        chatService.quitChatRoom(Long.valueOf(userDetails.getUsername()), roomId);

        return ResponseEntity.ok(ApiResponse.of(null, ChatResponse.QUIT_CHAT_ROOM.getMessage()));
    }


}
