package com.example.eat_together.domain.chat.dto;

import com.example.eat_together.domain.chat.chatEnum.ChatGroupStatus;
import com.example.eat_together.domain.chat.chatEnum.FoodType;
import com.example.eat_together.domain.chat.entity.ChatGroup;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//기본 생성자 getter 필수
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatGroupUpdateRequestDto {

    private String title;

    private String description;

    private FoodType foodType;

    private Integer maxMember;

    private ChatGroupStatus chatGroupStatus;

}
