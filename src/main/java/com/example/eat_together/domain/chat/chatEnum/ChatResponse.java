package com.example.eat_together.domain.chat.chatEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatResponse {
    CHATTING_GROUP_CREATED("채팅 그룹을 생성했습니다.");

    private final String message;

}

