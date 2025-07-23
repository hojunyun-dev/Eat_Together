package com.example.eat_together.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {


    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),

    // 400 BAD_REQUEST
    INFO_MISMATCH(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 다릅니다."),
    PASSWORD_WRONG(HttpStatus.BAD_REQUEST, "비밀번호가 다릅니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UPDATE_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "변경 내용을 입력해야 합니다."),
    DELETED_USER(HttpStatus.BAD_REQUEST, "삭제된 유저입니다"),

    // 403 FORBIDEN
    ADMIN_ACCOUNT_CANNOT_BE_DELETED(HttpStatus.FORBIDDEN, "관리자 계정은 삭제할 수 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),

    // 404 NOT_FOUND
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "매장을 찾을 수 없습니다."),
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 장바구니를 찾을 수 없습니다."),
    NOT_FOUND_CHAT_ROOM(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "아이템을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문을 찾을 수 없습니다."),

    // 409 CONFLICT
    DUPLICATE_USER(HttpStatus.CONFLICT, "중복된 유저가 있습니다"),
    STORE_ALREADY_OPEN(HttpStatus.CONFLICT, "이미 영업 중인 매장입니다."),
    STORE_ALREADY_CLOSED(HttpStatus.CONFLICT, "이미 영업 종료된 매장입니다.");


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
