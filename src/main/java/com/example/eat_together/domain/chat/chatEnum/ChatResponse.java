package com.example.eat_together.domain.chat.chatEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatResponse {
    CREATE_CHAT_ROOM("채팅방을 생성합니다."),
    PARTICIPATE_CHAT_ROOM("채팅에에 참여합니다."),
    READ_CHAT_ROOM_LIST("채팅방을 조회합니다."),
    READ_CHAT_MESSAGE_LIST("채팅 내역을 조회합니다."),
    QUIT_CHAT_ROOM("채팅방에서 퇴장합니다.");

    private final String message;

}

