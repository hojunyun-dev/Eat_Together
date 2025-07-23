package com.example.eat_together.domain.chat.service;

import com.example.eat_together.domain.chat.dto.ChatGroupDto;
import com.example.eat_together.domain.chat.entity.ChatGroup;
import com.example.eat_together.domain.chat.entity.ChatRoom;
import com.example.eat_together.domain.chat.repository.ChatGroupRepository;
import com.example.eat_together.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatGroupRepository chatGroupRepository;
    private final ChatRoomRepository chatRoomRepository;
    public void createChatGroup(ChatGroupDto chatGroupDto) {
        ChatGroup chatGroup = ChatGroup.of(chatGroupDto);
        ChatRoom chatRoom = ChatRoom.of(chatGroup);
        chatGroupRepository.save(chatGroup);
        chatRoomRepository.save(chatRoom);
    }
}
