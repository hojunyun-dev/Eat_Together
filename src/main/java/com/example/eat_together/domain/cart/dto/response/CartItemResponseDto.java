package com.example.eat_together.domain.cart.dto.response;

import com.example.eat_together.domain.cart.entity.CartItem;
import lombok.Builder;
import lombok.Getter;

/**
 * 장바구니 항목 응답 정보를 담는 DTO
 */
@Getter
@Builder
public class CartItemResponseDto {

    private final Long itemId;
    private final String menuName;
    private final int quantity;
    private final double price;
    private final double totalPrice;

    public static CartItemResponseDto from(CartItem cartItem) {
        return CartItemResponseDto.builder()
                .itemId(cartItem.getId())
                .menuName(cartItem.getMenu().getName())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getMenu().getPrice())
                .totalPrice(cartItem.getTotalPrice())
                .build();
    }
}
