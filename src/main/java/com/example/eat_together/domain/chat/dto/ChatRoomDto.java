package com.example.eat_together.domain.chat.dto;

import com.example.eat_together.domain.chat.chatEnum.FoodType;
import com.example.eat_together.domain.chat.chatEnum.ChatGroupStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto {
    private Long roomId;
    private String title;
    private String description;
    private FoodType foodType;
    private Integer maxMember;
    private ChatGroupStatus chatGroupStatus;
    private Long currentMemberCount;

    public static ChatRoomDto of(Long roomId, String title, String description, FoodType foodType, Integer maxMember, ChatGroupStatus chatGroupStatus, Long currentMemberCount) {
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.roomId = roomId;
        chatRoomDto.title = title;
        chatRoomDto.description = description;
        chatRoomDto.foodType = foodType;
        chatRoomDto.maxMember = maxMember;
        chatRoomDto.chatGroupStatus = chatGroupStatus;
        chatRoomDto.currentMemberCount = currentMemberCount;

        return chatRoomDto;
    }

}
