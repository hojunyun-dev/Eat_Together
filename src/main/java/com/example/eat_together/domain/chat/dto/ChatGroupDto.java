package com.example.eat_together.domain.chat.dto;

import com.example.eat_together.domain.chat.chatEnum.FoodType;
import com.example.eat_together.domain.chat.chatEnum.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatGroupDto {
    @NotNull
    private String title;

    private String description;

    private FoodType foodType;

    private Integer maxMember;

    private Status status;
}
