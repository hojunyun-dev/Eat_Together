package com.example.eat_together.user.dto.request;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private final Long userId;
    private final String loginId;
    private final String password;

    public LoginRequestDto(Long userId, String loginId, String password) {
        this.userId = userId;
        this.loginId = loginId;
        this.password = password;
    }
}
