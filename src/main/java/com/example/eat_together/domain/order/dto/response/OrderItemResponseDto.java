package com.example.eat_together.domain.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OrderItemResponseDto {
    private final Long menuId;
    private final String menuName;
    private final int quantity;
    private final double price;
    private final double totalPrice;
}
