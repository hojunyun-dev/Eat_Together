package com.example.eat_together.domain.chat.dto;

import com.example.eat_together.domain.chat.chatEnum.ChatGroupStatus;
import com.example.eat_together.domain.chat.chatEnum.FoodType;
import com.example.eat_together.domain.chat.entity.ChatGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
//기본 생성자 getter 필수
@Getter
@NoArgsConstructor
public class ChatGroupCreateRequestDto {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String description;

    private FoodType foodType;

    private Integer maxMember;

    private ChatGroupStatus chatGroupStatus;

    public static ChatGroupCreateRequestDto of(ChatGroup chatGroup) {
        ChatGroupCreateRequestDto chatGroupCreateRequestDto = new ChatGroupCreateRequestDto();
        chatGroupCreateRequestDto.title = chatGroup.getTitle();
        chatGroupCreateRequestDto.description = chatGroup.getDescription();
        chatGroupCreateRequestDto.foodType = chatGroup.getFoodType();
        chatGroupCreateRequestDto.maxMember = chatGroup.getMaxMember();
        chatGroupCreateRequestDto.chatGroupStatus = chatGroup.getChatGroupStatus();

        return chatGroupCreateRequestDto;
    }
}
