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

    /**
     * {@link CartItem} 엔티티를 기반으로 {@code CartItemResponseDto} 생성
     *
     * @param cartItem 장바구니 항목 엔티티
     * @return 변환된 DTO 객체
     */
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
