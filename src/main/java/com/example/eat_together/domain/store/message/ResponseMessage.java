package com.example.eat_together.domain.store.message;

import lombok.Getter;

@Getter
public enum ResponseMessage {


    STORE_CREATED_SUCCESS("매장 등록이 완료되었습니다."),
    STORE_LIST_FETCH_SUCCESS("매장 목록 조회가 완료되었습니다."),
    STORE_MY_LIST_FETCH_SUCCESS("내 매장 목록 조회가 완료되었습니다."),
    STORE_FETCH_SUCCESS("매장 조회가 완료되었습니다."),
    STORE_SEARCH_SUCCESS("매장 검색이 완료되었습니다."),
    STORE_UPDATED_SUCCESS("매장 수정이 완료되었습니다."),
    STORE_DELETED_SUCCESS("매장 삭제가 완료되었습니다."),
    STORE_OPEN_SUCCESS("매장이 영업 상태로 변경되었습니다."),
    STORE_CLOSE_SUCCESS("매장이 영업 종료 상태로 변경되었습니다.");


    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }
}