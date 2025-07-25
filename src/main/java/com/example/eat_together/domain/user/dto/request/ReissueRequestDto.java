package com.example.eat_together.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueRequestDto {

    @NotBlank(message = "RefreshToken 을 입력해주세요.")
    private String refreshToken;

}