package com.example.eat_together.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDto {

    private final Long userId;

    @NotBlank(message = "아이디는 필수입력값입니다.")
    private final String loginId;

    @NotBlank(message = "비밀번호는 필수입력값입니다.")
    private final String password;

    public LoginRequestDto(Long userId, String loginId, String password) {
        this.userId = userId;
        this.loginId = loginId;
        this.password = password;
    }
}
