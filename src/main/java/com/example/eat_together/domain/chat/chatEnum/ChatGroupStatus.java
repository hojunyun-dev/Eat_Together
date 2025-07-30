package com.example.eat_together.domain.chat.chatEnum;

public enum ChatGroupStatus {
    //아직 인원 다 찬 상태나 만료 조건 및 상태는 구현하지 않았습니다.
    //방 생성 시 기본 상태
    OPEN,
    //호스트 외 한 명이라도 참가 시 상태 변경
    IN_PROGRESS,
    //인원 다 찬 상태
    FULL,
    //방이 종료된 상태
    EXPIRED
}
