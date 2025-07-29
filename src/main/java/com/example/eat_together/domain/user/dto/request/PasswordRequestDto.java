package com.example.eat_together.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true) // Jakcson이 역직렬화할 수 있도록 기본 생성자 강제 생성
public class PasswordRequestDto {
    @NotBlank(message = "비밀번호는 필수입력값 입니다.")
    private final String password;
}
