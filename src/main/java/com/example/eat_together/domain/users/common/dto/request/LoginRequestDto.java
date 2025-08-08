package com.example.eat_together.domain.users.common.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true) // Jakcson이 역직렬화할 수 있도록 기본 생성자 강제 생성
public class LoginRequestDto {

    private Long userId;

    @NotBlank(message = "아이디는 필수입력값입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입력값입니다.")
    private String password;

    public LoginRequestDto(Long userId, String loginId, String password) {
        this.userId = userId;
        this.loginId = loginId;
        this.password = password;
    }
}
