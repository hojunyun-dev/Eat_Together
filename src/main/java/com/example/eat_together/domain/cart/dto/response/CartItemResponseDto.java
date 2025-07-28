package com.example.eat_together.domain.cart.dto.response;

import com.example.eat_together.domain.cart.entity.CartItem;
import lombok.Getter;

@Getter
public class CartItemResponseDto {

    private final Long itemId;
    private final String menuName;
    private final int quantity;
    private final double price;          // 단일 메뉴 가격 (소수점 가능)
    private final double totalPrice;     // 수량 * 가격

    public CartItemResponseDto(CartItem cartItem) {
        this.itemId = cartItem.getId();
        this.menuName = cartItem.getMenu().getName();
        this.quantity = cartItem.getQuantity();
        this.price = cartItem.getMenu().getPrice(); // double로 바로 저장
        this.totalPrice = cartItem.getTotalPrice(); // 메뉴 엔티티에서 double로 계산된 totalPrice 반환
    }
}
