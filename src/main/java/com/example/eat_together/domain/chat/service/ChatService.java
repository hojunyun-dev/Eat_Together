package com.example.eat_together.domain.chat.service;

import com.example.eat_together.domain.chat.dto.ChatGroupDto;
import com.example.eat_together.domain.chat.dto.ChatMessageRequestDto;
import com.example.eat_together.domain.chat.dto.ChatMessageResponseDto;
import com.example.eat_together.domain.chat.dto.ChatRoomDto;
import com.example.eat_together.domain.chat.entity.ChatGroup;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final UserRepository userRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;

    public void createChatGroup(Long userId,  ChatGroupDto chatGroupDto) {
        Optional<User> optionalHost = userRepository.findById(userId);
        if(optionalHost.isEmpty())
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        User host = optionalHost.get();
        ChatGroup chatGroup = ChatGroup.of(host, chatGroupDto);
        ChatRoom chatRoom = ChatRoom.of(chatGroup);
        chatGroupRepository.save(chatGroup);
        chatRoomRepository.save(chatRoom);
        ChatRoomUser chatRoomUser = ChatRoomUser.of(chatRoom, host);
        chatRoomUserRepository.save(chatRoomUser);
    }

    //그룹 멤버 저장
    public void enterChatRoom(Long userId, Long roomId) {
        // 사용자 정보
        User user = getUser(userId);
        // 채팅방 정보
        ChatRoom chatRoom = getChatRoom(roomId);
        // 사용자를 채팅방 참여 사용자로 저장
        if(!isGroupMember(userId, roomId)){
            ChatRoomUser chatRoomUser = ChatRoomUser.of(chatRoom, user);
            chatRoomUserRepository.save(chatRoomUser);
        }
    }

    public List<ChatRoomDto> getChatRoomList() {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
        List<ChatRoomDto> chatRoomDtoList = chatRoomList.stream()
                .map(chatRoom -> ChatRoomDto
                        .of(
                            chatRoom.getId(),
                            chatRoom.getChatGroup().getTitle(),
                            chatRoom.getChatGroup().getDescription(),
                            chatRoom.getChatGroup().getFoodType(),
                            chatRoom.getChatGroup().getMaxMember(),
                            chatRoom.getChatGroup().getStatus(),
                            chatRoomUserRepository.countByChatRoomId(chatRoom.getId()))
                            ).toList();

        return chatRoomDtoList;
    }

    public List<ChatMessageResponseDto> getChatMessageList(Long roomId) {
        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomId(roomId);
        List<ChatMessageResponseDto> chatMessageResponseDtoList = chatMessageList.stream()
                .map(chatMessage -> ChatMessageResponseDto
                        .of(chatMessage.getUser().getUserId(),
                                chatMessage.getChatRoom().getId(),
                                chatMessage.getMessage(),
                                chatMessage.getUpdatedAt()
                                )).toList();

        return chatMessageResponseDtoList;
    }

    public void quitChatRoom(Long userId, Long roomId) {

        if(isGroupMember(userId, roomId)){
            System.out.println("==============================================================================================================");
//            ChatRoomUser chatRoomUser = chatRoomUserRepository.findByUserIdAndRoomId(userId, roomId);
            chatRoomUserRepository.deleteByUserIdAndRoomId(userId, roomId);
        }
    }



    /*
    활용 메서드
     */

    //메세지 저장
    public void saveMessage(ChatMessageRequestDto chatMessageRequestDto, Long userId, Long roomId){
        User user = getUser(userId);
        ChatRoom chatRoom = getChatRoom(roomId);

        ChatMessage chatMessage = ChatMessage.of(chatMessageRequestDto, user, chatRoom);
        chatMessageRepository.save(chatMessage);
    }

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
