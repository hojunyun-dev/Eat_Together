package com.example.eat_together.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemResponseDto {
    private Long menuId;
    private String menuName;
    private int quantity;
    private double price;
    private double totalPrice;
}
