package com.example.eat_together.domain.rider.dto.request;

import com.example.eat_together.domain.rider.riderEnum.RiderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RiderStatusRequestDto {

    @NotNull(message = "라이더 상태는 필수입니다.")
    private RiderStatus status;
}
