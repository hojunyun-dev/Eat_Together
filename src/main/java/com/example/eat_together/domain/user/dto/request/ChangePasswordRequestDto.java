package com.example.eat_together.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangePasswordRequestDto {

    @NotBlank(message = "바꿀 비밀번호는 필수입력값입니다.")
    private final String newPassword;

    @NotBlank(message = "현재 비밀번호는 필수입력값입니다.")
    private final String oldPassword;

    public ChangePasswordRequestDto(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
