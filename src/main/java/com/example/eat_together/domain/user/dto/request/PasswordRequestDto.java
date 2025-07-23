package com.example.eat_together.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class PasswordRequestDto {
    @NotBlank(message = "비밀번호는 필수입력값 입니다.")
    private final String password;
}
