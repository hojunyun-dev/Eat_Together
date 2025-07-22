package com.example.eat_together.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class UpdateUserInfoRequestDto {

    @Email(message = "올바른 이메일 형식이여야 합니다")
    private final String email;

    private final String name;

    private final String nickname;

    public UpdateUserInfoRequestDto(String email, String name, String nickname) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
    }
}
