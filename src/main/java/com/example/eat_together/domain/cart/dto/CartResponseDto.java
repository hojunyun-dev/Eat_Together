package com.example.eat_together.domain.cart.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CartResponseDto {

    private final List<CartItemResponseDto> items;
    private final double totalPrice;

    public CartResponseDto(List<CartItemResponseDto> items) {
        this.items = items;
        this.totalPrice = items.stream().mapToDouble(CartItemResponseDto::getTotalPrice).sum();
    }
}
