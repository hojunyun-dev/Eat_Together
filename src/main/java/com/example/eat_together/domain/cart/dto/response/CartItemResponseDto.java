package com.example.eat_together.domain.cart.dto.response;

import com.example.eat_together.domain.cart.entity.CartItem;
import lombok.Getter;

/**
 * 장바구니 항목 응답 정보를 담는 DTO
 */
@Getter
public class CartItemResponseDto {

    private final Long itemId;
    private final String menuName;
    private final int quantity;
    private final double price;
    private final double totalPrice;

    /**
     * CartItem 엔티티 기반 응답 DTO 생성자
     *
     * @param cartItem 장바구니 항목 엔티티
     */
    public CartItemResponseDto(CartItem cartItem) {
        this.itemId = cartItem.getId();
        this.menuName = cartItem.getMenu().getName();
        this.quantity = cartItem.getQuantity();
        this.price = cartItem.getMenu().getPrice();
        this.totalPrice = cartItem.getTotalPrice();
    }
}
