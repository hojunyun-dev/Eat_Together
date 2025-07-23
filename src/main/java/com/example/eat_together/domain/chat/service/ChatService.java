package com.example.eat_together.domain.chat.service;

import com.example.eat_together.domain.chat.dto.ChatGroupDto;
import com.example.eat_together.domain.chat.entity.ChatGroup;
import com.example.eat_together.domain.chat.entity.ChatRoom;
import com.example.eat_together.domain.chat.entity.ChatRoomUser;
import com.example.eat_together.domain.chat.repository.ChatGroupRepository;
import com.example.eat_together.domain.chat.repository.ChatRoomRepository;
import com.example.eat_together.domain.chat.repository.ChatRoomUserRepository;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final UserRepository userRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    public void createChatGroup(Long loginId,  ChatGroupDto chatGroupDto) {
        Optional<User> optionalHost = userRepository.findById(loginId);
        if(optionalHost.isEmpty())
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        User host = optionalHost.get();
        ChatGroup chatGroup = ChatGroup.of(host, chatGroupDto);
        ChatRoom chatRoom = ChatRoom.of(chatGroup);
        chatGroupRepository.save(chatGroup);
        chatRoomRepository.save(chatRoom);
    }

    //그룹 멤버 저장
    public void enterChatRoom(Long loginId, Long roomId) {
        // 사용자 정보
        User user = getUser(loginId);
        // 채팅방 정보
        ChatRoom chatRoom = getChatRoom(roomId);
        // 사용자를 채팅방 참여 사용자로 저장
        if(!isGroupMember(loginId, roomId)){
            ChatRoomUser chatRoomUser = ChatRoomUser.of(chatRoom, user);
            chatRoomUserRepository.save(chatRoomUser);
        }
    }

    //메세지 저장


    //사용자
    private User getUser(Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty())
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        User user = optionalUser.get();

        return user;
    }

    //채팅방
    private ChatRoom getChatRoom(Long roomId){
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(roomId);
        if(optionalChatRoom.isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM);
        ChatRoom chatRoom = optionalChatRoom.get();

        return chatRoom;
    }

    //리스트 반환
    private List<ChatRoomUser> getList(Long roomId){
        ChatRoom chatRoom = getChatRoom(roomId);
        List<ChatRoomUser> chatRoomUserList = chatRoom.getChatRoomUserList();

        return chatRoomUserList;
    }

    // 그룹 멤버 여부 확인
    private boolean isGroupMember(Long userId, Long roomId){
        System.out.println("userId: " + userId);
        List<ChatRoomUser> chatRoomUserList = getList(roomId);
        for(ChatRoomUser chatRoomUser : chatRoomUserList){
            Long matchId = chatRoomUser.getUser().getUserId();
            if(matchId.equals(userId)) {
                System.out.println("userId: " + matchId);
                return true;
            }
        }
        return false;
    }
}
