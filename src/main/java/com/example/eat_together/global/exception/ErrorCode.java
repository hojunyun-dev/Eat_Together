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
    STORE_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "자신의 매장이 아닙니다."),
    STORE_INVALID_TIME(HttpStatus.BAD_REQUEST, "오픈 시간이 종료 시간보다 빨라야 합니다."),
    ORDER_INVALID_PERIOD(HttpStatus.BAD_REQUEST, "조회 시작일은 종료일보다 이전이어야 합니다."),
    ORDER_PERIOD_MISMATCH(HttpStatus.BAD_REQUEST, "조회 시작일과 종료일은 함께 입력되어야 합니다."),
    INVALID_SESSION(HttpStatus.BAD_REQUEST, "유효하지 않은 세션입니다."),
    INVALID_URI(HttpStatus.BAD_REQUEST, "유효하지 않은 URI입니다."),
    USER_NOT_DELETE(HttpStatus.BAD_REQUEST,"삭제되지 않은 유저입니다."),
    CART_EXCEEDS_MAX_QUANTITY(HttpStatus.BAD_REQUEST, "메뉴는 최대 99개까지 담을 수 있습니다."),
    CART_INVALID_STORE(HttpStatus.BAD_REQUEST, "기존 장바구니와 다른 매장의 메뉴는 담을 수 없습니다."),

    // 403 FORBIDDEN
    ADMIN_ACCOUNT_CANNOT_BE_DELETED(HttpStatus.FORBIDDEN, "관리자 계정은 삭제할 수 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.FORBIDDEN,"Refresh Token이 만료되었습니다."),

    // 404 NOT_FOUND
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "매장을 찾을 수 없습니다."),
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 장바구니를 찾을 수 없습니다."),
    NOT_FOUND_CHAT_ROOM(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "아이템을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문을 찾을 수 없습니다."),
    STORE_SEARCH_NO_RESULT(HttpStatus.NOT_FOUND, "검색 결과가 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.NOT_FOUND,"Refresh Token을 찾을 수 없습니다."),
    INVALID_SEARCH_CRITERIA(HttpStatus.NOT_FOUND,"검색된 결과가 없습니다."),

    // 409 CONFLICT
    DUPLICATE_USER(HttpStatus.CONFLICT, "중복된 유저가 있습니다"),
    STORE_ALREADY_OPEN(HttpStatus.CONFLICT, "이미 영업 중인 매장입니다."),
    STORE_ALREADY_CLOSED(HttpStatus.CONFLICT, "이미 영업 종료된 매장입니다."),
    STORE_NAME_DUPLICATED(HttpStatus.CONFLICT, "동일한 이름의 매장을 등록할 수 없습니다."),
    MENU_NAME_DUPLICATED(HttpStatus.CONFLICT, "매장에 동일한 이름의 메뉴를 등록할 수 없습니다."),
    DUPLICATE_ORDER(HttpStatus.CONFLICT, "이미 처리 중인 주문이 있습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
