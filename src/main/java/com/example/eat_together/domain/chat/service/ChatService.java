package com.example.eat_together.domain.chat.service;

import com.example.eat_together.domain.chat.chatEnum.ChatGroupStatus;
import com.example.eat_together.domain.chat.chatEnum.FoodType;
import com.example.eat_together.domain.chat.chatEnum.MemberRole;
import com.example.eat_together.domain.chat.dto.*;
import com.example.eat_together.domain.chat.entity.ChatGroup;
import com.example.eat_together.domain.chat.entity.ChatMessage;
import com.example.eat_together.domain.chat.entity.ChatRoom;
import com.example.eat_together.domain.chat.entity.ChatRoomUser;
import com.example.eat_together.domain.chat.repository.ChatGroupRepository;
import com.example.eat_together.domain.chat.repository.ChatMessageRepository;
import com.example.eat_together.domain.chat.repository.ChatRoomRepository;
import com.example.eat_together.domain.chat.repository.ChatRoomUserRepository;
import com.example.eat_together.domain.users.common.entity.User;
import com.example.eat_together.domain.users.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ChatUtil chatUtil;
    private final NullIgnoreMapper nullIgnoreMapper;


    //채팅방 생성
    @Transactional
    public void createChatGroup(Long userId, ChatGroupCreateRequestDto chatGroupCreateRequestDto) {
        Optional<User> optionalHost = userRepository.findById(userId);
        if (optionalHost.isEmpty())
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        User host = optionalHost.get();
        ChatGroup chatGroup = ChatGroup.of(host, chatGroupCreateRequestDto);
        //chatGroup 생성 시 chatRoom 함께 생성
        ChatRoom chatRoom = ChatRoom.of(chatGroup);
        ChatRoomUser chatRoomUser = ChatRoomUser.of(chatRoom, host, MemberRole.HOST);

        chatGroupRepository.save(chatGroup);
        chatRoomRepository.save(chatRoom);
        chatRoomUserRepository.save(chatRoomUser);
    }

    /* 그룹 멤버 저장 */
    @Transactional
    public boolean enterChatRoom(Long userId, Long roomId) {
        ChatRoom chatRoom = chatUtil.getChatRoom(roomId);
        ChatGroup chatGroup = chatRoom.getChatGroup();

        //현재 멤버 수 확인
        Long memberCount = chatRoomUserRepository.countByChatRoomId(roomId);
        //제한 인원 확인
        Long maxMember = Long.valueOf(chatGroup.getMaxMember());

        //이미 멤버인 경우: 참가x 입장o
        if(chatUtil.getGroupMember(userId, roomId) != null)
            return true;
        else if (memberCount >= 1L && memberCount < maxMember){
            chatUtil.saveNewMember(chatRoom, userId);
            memberCount = chatRoomUserRepository.countByChatRoomId(roomId);
            chatRoom.updateCount(memberCount);

            //호스트 포함 두 명 참가 시 상태 변경
            if(memberCount.equals(2L)) {
                chatGroup.updateStatus(ChatGroupStatus.IN_PROGRESS);
            }
            //제한 인원 도달 시 상태 변경
            if(memberCount.equals(maxMember)) {
                chatGroup.updateStatus(ChatGroupStatus.FULL);
            }
            return false;
        }//멤버가 아니며, 채팅방 만 원일 시 참가 불가
        else {
            throw new CustomException(ErrorCode.ENTER_CHAT_ROOM_INAVAILABLE);
        }
    }

    /* 채팅방 목록 조회 */
    @Transactional
    public List<ChatRoomDto> getChatRoomList(FoodType foodType, String keyWord) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll(foodType, keyWord);
        List<ChatRoomDto> chatRoomDtoList = chatRoomList.stream()
                .map(chatRoom -> ChatRoomDto
                        .of(
                                chatRoom.getId(),
                                chatRoom.getChatGroup().getTitle(),
                                chatRoom.getChatGroup().getDescription(),
                                chatRoom.getChatGroup().getFoodType(),
                                chatRoom.getChatGroup().getMaxMember(),
                                chatRoom.getChatGroup().getChatGroupStatus(),
                                chatRoom.getCurrentMemberCount())
                ).toList();

        return chatRoomDtoList;
    }

    // 채팅방 정보 수정
    @Transactional
    public void updateChatGroup(Long userId, Long roomId, ChatGroupUpdateRequestDto chatGroupUpdateRequestDto){
        if(chatUtil.getMemberRole(userId, roomId) == (MemberRole.MEMBER))
            throw new CustomException(ErrorCode.NOT_HOST);
        ChatGroup chatGroup = chatUtil.getChatGroup(roomId);

        chatGroup = nullIgnoreMapper.updateChatGroup(chatGroupUpdateRequestDto, chatGroup.toBuilder());
        chatGroupRepository.save(chatGroup);
    }

    // 채팅방 퇴장: 멤버에서 삭제
    @Transactional
    public void quitChatRoom(Long userId, Long roomId) {
        if (chatUtil.getGroupMember(userId, roomId) != null)
            chatRoomUserRepository.deleteByUserIdAndRoomId(userId, roomId);
    }

    // 채팅방 삭제
    @Transactional
    public void deleteChatRoom(Long userId, Long roomId) {
        MemberRole memberRole = chatUtil.getMemberRole(userId, roomId);
        ChatGroup chatGroup = chatUtil.getChatGroup(roomId);
        if (chatUtil.getGroupMember(userId, roomId) != null && memberRole == MemberRole.HOST) {
            chatGroupRepository.delete(chatGroup);
        } else
            throw new CustomException(ErrorCode.NOT_HOST);
    }

    /* 메세지 내역 조회 */
    @Transactional
    public List<ChatMessageResponseDto> getChatMessageList(Long roomId) {
        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomId(roomId);
        List<ChatMessageResponseDto> chatMessageResponseDtoList = chatMessageList.stream()
                .map(chatMessage -> ChatMessageResponseDto
                        .of(chatMessage.getUser().getUserId(),
                                chatMessage.getChatRoom().getId(),
                                chatMessage.getMessage()
                        )).toList();

        return chatMessageResponseDtoList;
    }
}
