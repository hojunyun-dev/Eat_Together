package com.example.eat_together.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @NotBlank(message = "아이디는 필수입력값 입니다")
    @Size(min = 4, max = 20, message = "아이디는 4-20자 사이로 입력해주세요")
    private final String loginId;

    @NotBlank(message = "이름은 필수입력값 입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 사이로 입력해주세요")
    private final String name;

    @NotBlank(message = "비밀번호는 필수입력값 입니다")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 최소 8자 이상이어야 하며, 영문(대소문자 무관), 숫자, 특수문자(@$!%*?&)를 각각 최소 1개 포함해야 합니다."
    )
    private final String password;

    @Email
    @NotBlank(message = "이메일은 필수입력값 입니다")
    private final String email;

    @NotBlank(message = "닉네임은 필수입력값 입니다")
    @Size(min = 4, max = 20, message = "닉네임은 4-20자 사이로 입력해주세요")
    private final String nickname;

    public SignupRequestDto(String loginId, String name, String password, String email, String nickname) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }
}
