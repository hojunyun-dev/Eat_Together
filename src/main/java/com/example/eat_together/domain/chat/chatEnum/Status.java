package com.example.eat_together.domain.chat.chatEnum;

public enum Status {
    //아직 인원 다 찬 상태나 만료 조건 및 상태는 구현하지 않았습니다.
    //방 생성 시 기본 상태
    IN_PROGRESS,
    //인원 다 찬 상태
    FULL,
    //방이 종료된 상태
    EXPIRED
}
