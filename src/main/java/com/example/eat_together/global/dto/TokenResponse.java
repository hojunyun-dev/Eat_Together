package com.example.eat_together.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class TokenResponse {

    private final String accessToken;
    private final String refreshToken;

}
