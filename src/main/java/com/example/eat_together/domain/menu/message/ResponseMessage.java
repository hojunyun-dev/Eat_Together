package com.example.eat_together.domain.menu.message;

import lombok.Getter;

@Getter
public enum ResponseMessage {


    MENU_CREATED_SUCCESS("메뉴 등록이 완료되었습니다."),
    MENU_LIST_FETCH_SUCCESS("메뉴 목록 조회가 완료되었습니다."),
    MENU_FETCH_SUCCESS("메뉴 조회가 완료되었습니다.");


    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }
}
