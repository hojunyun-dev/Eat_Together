package com.example.eat_together.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @NotBlank(message = "아이디는 필수입력값 입니다")
    private final String loginId;

    @NotBlank(message = "비밀번호는 필수입력값 입니다")
    private final String password;

    @Email
    @NotBlank(message = "이메일은 필수입력값 입니다")
    private final String email;

    @NotBlank(message = "닉네임은 필수입력값 입니다")
    private final String nickname;

    public SignupRequestDto(String loginId, String password, String email, String nickname) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }

}
