package com.example.eat_together.user.dto.request;

import lombok.Getter;

@Getter
public class LoginRequestDto {

    private final String loginId;
    private final String password;

    public LoginRequestDto(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
