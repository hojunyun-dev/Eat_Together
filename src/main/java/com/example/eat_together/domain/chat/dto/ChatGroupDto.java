package com.example.eat_together.domain.chat.dto;

import com.example.eat_together.domain.chat.chatEnum.FoodType;
import com.example.eat_together.domain.chat.chatEnum.Status;
import com.example.eat_together.domain.chat.entity.ChatGroup;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatGroupDto {
    @NotNull
    private String title;

    private String description;

    private FoodType foodType;

    private Integer maxMember;

    private Status status;

    public static ChatGroupDto of(ChatGroup chatGroup){
        ChatGroupDto chatGroupDto = new ChatGroupDto();
        chatGroupDto.title = chatGroup.getTitle();
        chatGroupDto.description = chatGroup.getDescription();
        chatGroupDto.foodType = chatGroup.getFoodType();
        chatGroupDto.maxMember = chatGroup.getMaxMember();
        chatGroupDto.status = chatGroup.getStatus();

        return chatGroupDto;
    }
}
