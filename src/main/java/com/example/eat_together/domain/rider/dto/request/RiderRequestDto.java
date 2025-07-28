package com.example.eat_together.domain.rider.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class RiderRequestDto {

    //name필드 삭제

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리 숫자여야 합니다.")
    private String phone;

    @NotNull(message = "오픈 시간은 필수입니다.")
    private LocalTime openTime;

    @NotNull(message = "종료 시간은 필수입니다.")
    private LocalTime closeTime;

}

