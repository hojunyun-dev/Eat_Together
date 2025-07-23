package com.example.eat_together.domain.user.enums;

import lombok.Getter;

@Getter
public enum MessageEnum {
    LOGIN("로그인 성공!"),
    SIGNUP("회원가입 성공!"),
    CHANGE_PASSWORD("비밀번호 변경 완료!"),
    UPDATE_INFO("개인 정보 수정 완료!"),
    SEARCH_INFO("정보 조회 성공!"),
    DELETE_USER("유저 삭제 완료");

    private final String message;

    MessageEnum(String message){
        this.message=message;
    }
}
