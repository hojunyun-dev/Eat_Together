package com.example.eat_together.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CartItemRequestDto {

    @NotNull(message = "메뉴 ID는 필수입니다.")
    private Long menuId;

    @Min(value = 1, message = "수량은 최소 1 이상이어야 합니다.")
    private int quantity;
}
