package com.example.eat_together.domain.cart.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CartResponseDto {

    private final Long storeId;
    private final List<CartItemResponseDto> content;
    private final double subPrice;
    private final double deliveryTip;
    private final double storeTotalPrice;

    public CartResponseDto(Long storeId, List<CartItemResponseDto> content, double deliveryTip) {
        this.storeId = storeId;
        this.content = content;
        this.subPrice = content.stream().mapToDouble(CartItemResponseDto::getTotalPrice).sum();
        this.deliveryTip = deliveryTip;
        this.storeTotalPrice = subPrice + deliveryTip;
    }
}

