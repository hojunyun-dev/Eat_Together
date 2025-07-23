package com.example.eat_together.domain.cart.dto;

import com.example.eat_together.domain.cart.entity.CartItem;
import lombok.Getter;

@Getter
public class CartItemResponseDto {

    private final Long itemId;
    private final String menuName;
    private final int quantity;
    private final int price;         // 단일 메뉴 가격
    private final int totalPrice;    // 수량 * 가격

    public CartItemResponseDto(CartItem cartItem) {
        this.itemId = cartItem.getId();
        this.menuName = cartItem.getMenu().getName();
        this.quantity = cartItem.getQuantity();
        this.price = cartItem.getMenu().getPrice();
        this.totalPrice = cartItem.getTotalPrice();
    }
}
