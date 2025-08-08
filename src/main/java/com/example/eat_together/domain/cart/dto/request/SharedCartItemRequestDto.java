package com.example.eat_together.domain.cart.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SharedCartItemRequestDto {

    @NotNull(message = "메뉴 ID는 필수입니다.")
    private Long menuId;

    @Min(value = 1, message = "수량은 최소 1 이상이어야 합니다.")
    @Max(value = 99, message = "수량은 최대 99까지 허용됩니다.")
    private int quantity;
}
