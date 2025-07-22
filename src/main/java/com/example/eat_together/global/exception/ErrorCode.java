package com.example.eat_together.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    INFO_MISMATCH(HttpStatus.BAD_REQUEST,"아이디 또는 비밀번호가 다릅니다."),
    PASSWORD_WRONG(HttpStatus.BAD_REQUEST,"아이디 또는 비밀번호가 다릅니다."),
    DUPLICATE_USER(HttpStatus.CONFLICT,"중복된 유저가 있습니다");
    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
