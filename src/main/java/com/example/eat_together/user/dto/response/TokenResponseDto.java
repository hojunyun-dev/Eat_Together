package com.example.eat_together.user.dto.response;

import lombok.Getter;

@Getter
public class TokenResponseDto {

    private final String accessToken;

    public TokenResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
