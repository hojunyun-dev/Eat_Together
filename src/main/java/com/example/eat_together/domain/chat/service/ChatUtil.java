package com.example.eat_together.domain.chat.service;

import com.example.eat_together.domain.chat.chatEnum.MemberRole;
import com.example.eat_together.domain.chat.dto.ChatMessageRequestDto;
import com.example.eat_together.domain.chat.entity.ChatMessage;
import com.example.eat_together.domain.chat.entity.ChatRoom;
import com.example.eat_together.domain.chat.entity.ChatRoomUser;
import com.example.eat_together.domain.chat.repository.ChatGroupRepository;
import com.example.eat_together.domain.chat.repository.ChatMessageRepository;
import com.example.eat_together.domain.chat.repository.ChatRoomRepository;
import com.example.eat_together.domain.chat.repository.ChatRoomUserRepository;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChatUtil {

    protected final UserRepository userRepository;
    protected final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;

    //사용자
    protected User getUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        User user = optionalUser.get();

        return user;
    }

    //채팅방
    protected ChatRoom getChatRoom(Long roomId) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(roomId);
        if (optionalChatRoom.isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM);
        ChatRoom chatRoom = optionalChatRoom.get();

        return chatRoom;
    }

    //멤버 리스트 반환
    protected List<ChatRoomUser> getList(Long roomId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        List<ChatRoomUser> chatRoomUserList = chatRoom.getChatRoomUserList();

        return chatRoomUserList;
    }

    // 그룹 멤버 여부 확인
    public ChatRoomUser getGroupMember(Long userId, Long roomId) {
        List<ChatRoomUser> chatRoomUserList = getList(roomId);
        for (ChatRoomUser chatRoomUser : chatRoomUserList) {
            Long matchId = chatRoomUser.getUser().getUserId();
            if (matchId.equals(userId)) {
                return chatRoomUser;
            }
        }
        return null;
    }

    //멤버 추가
    protected void saveNewMember(ChatRoom chatRoom, Long userId){
        User user = getUser(userId);
        ChatRoomUser chatRoomUser = ChatRoomUser.of(chatRoom, user, MemberRole.MEMBER);
        chatRoomUserRepository.save(chatRoomUser);
    }

    //메세지 저장
    public void saveMessage(ChatMessageRequestDto chatMessageRequestDto, Long userId, Long roomId) {
        User user = getUser(userId);
        ChatRoom chatRoom = getChatRoom(roomId);

        ChatMessage chatMessage = ChatMessage.of(chatMessageRequestDto, user, chatRoom);
        chatMessageRepository.save(chatMessage);
    }

}
