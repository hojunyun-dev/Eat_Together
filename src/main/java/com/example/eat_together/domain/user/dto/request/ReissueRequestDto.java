package com.example.eat_together.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true) // Jakcson이 역직렬화할 수 있도록 기본 생성자 강제 생성
public class ReissueRequestDto {

    @NotBlank(message = "RefreshToken 을 입력해주세요.")
    private String refreshToken;

}