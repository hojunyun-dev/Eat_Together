package com.example.eat_together.domain.chat.controller;

import com.example.eat_together.domain.chat.chatEnum.ChatResponse;
import com.example.eat_together.domain.chat.chatEnum.FoodType;
import com.example.eat_together.domain.chat.dto.ChatGroupCreateRequestDto;
import com.example.eat_together.domain.chat.dto.ChatGroupUpdateRequestDto;
import com.example.eat_together.domain.chat.dto.ChatMessageResponseDto;
import com.example.eat_together.domain.chat.dto.ChatRoomDto;
import com.example.eat_together.domain.chat.service.ChatMessageRedisService;
import com.example.eat_together.domain.chat.service.ChatService;
import com.example.eat_together.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chats")
public class ChatManageController {
    private final ChatService chatService;
    private final ChatMessageRedisService chatMessageRedisService;

    //채팅방 생성
    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createChatGroup(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ChatGroupCreateRequestDto chatGroupCreateRequestDto) {
        chatService.createChatGroup(Long.valueOf(userDetails.getUsername()), chatGroupCreateRequestDto);

        return ResponseEntity.ok(ApiResponse.of(null, ChatResponse.CREATE_CHAT_ROOM.getMessage()));
    }

    //채팅방 참여 및 입장
    @PostMapping("/{roomId}/enter")
    public ResponseEntity<ApiResponse<Void>> enterChatRoom(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long roomId) {
        boolean result = chatService.enterChatRoom(Long.valueOf(userDetails.getUsername()), roomId);
        if(result)
            return ResponseEntity.ok(ApiResponse.of(null, ChatResponse.ENTER_CHAT_ROOM.getMessage()));
        else
            return ResponseEntity.ok(ApiResponse.of(null, ChatResponse.PARTICIPATE_CHAT_ROOM.getMessage()));
    }

    //채팅방 조회
    @GetMapping()
    public ResponseEntity<ApiResponse<List<ChatRoomDto>>> getChatRoomList(@RequestParam(required = false) FoodType foodType, @RequestParam(required = false) String keyWord) {
        List<ChatRoomDto> chatRoomDtoList = chatService.getChatRoomList(foodType, keyWord);

        return ResponseEntity.ok(ApiResponse.of(chatRoomDtoList, ChatResponse.READ_CHAT_ROOM_LIST.getMessage()));
    }

    //채팅방 수정
    @PatchMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Void>> updateChatGroup(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long roomId, @RequestBody ChatGroupUpdateRequestDto chatGroupUpdateRequestDto) {
        chatService.updateChatGroup(Long.valueOf(userDetails.getUsername()), roomId, chatGroupUpdateRequestDto);

        return ResponseEntity.ok(ApiResponse.of(null, ChatResponse.UPDATE_CHAT_ROOM.getMessage()));
    }

    //채팅방 퇴장
    @DeleteMapping("/{roomId}/quit")
    public ResponseEntity<ApiResponse<Void>> quitChatRoom(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long roomId) {
        chatService.quitChatRoom(Long.valueOf(userDetails.getUsername()), roomId);

        return ResponseEntity.ok(ApiResponse.of(null, ChatResponse.QUIT_CHAT_ROOM.getMessage()));
    }

    //채팅방 삭제
    @DeleteMapping("/{roomId}/remove")
    public ResponseEntity<ApiResponse<Void>> deleteChatRoom(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long roomId) {
        chatService.deleteChatRoom(Long.valueOf(userDetails.getUsername()), roomId);

        return ResponseEntity.ok(ApiResponse.of(null, ChatResponse.DELETE_CHAT_ROOM.getMessage()));
    }

    //기존 채팅 내역 조회
    @GetMapping("/{roomId}/v2")
    public ResponseEntity<ApiResponse<List<ChatMessageResponseDto>>> getChatMessageList(@PathVariable Long roomId) {
        List<ChatMessageResponseDto> chatMessageList = chatMessageRedisService.getMessageList(roomId);

        return ResponseEntity.ok(ApiResponse.of(chatMessageList, ChatResponse.READ_CHAT_MESSAGE_LIST.getMessage()));
    }

    @GetMapping("/{roomId}/v1")
    public ResponseEntity<ApiResponse<List<ChatMessageResponseDto>>> getChatMessageListV1(@PathVariable Long roomId) {
        List<ChatMessageResponseDto> chatMessageList = chatService.getChatMessageList(roomId);

        return ResponseEntity.ok(ApiResponse.of(chatMessageList, ChatResponse.READ_CHAT_MESSAGE_LIST.getMessage()));
    }
}
