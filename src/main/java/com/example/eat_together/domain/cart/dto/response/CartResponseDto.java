package com.example.eat_together.domain.cart.dto.response;

import com.example.eat_together.domain.cart.entity.Cart;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 장바구니 응답 정보를 담는 DTO
 */
@Getter
@Builder
public class CartResponseDto {

    private Long storeId;
    private List<CartItemResponseDto> content;
    private double subPrice;
    private double deliveryFee;
    private double storeTotalPrice;

    /**
     * {@link Cart} 엔티티를 기반으로 {@code CartResponseDto} 생성
     *
     * @param cart 장바구니 엔티티
     * @return 변환된 DTO 객체
     */
    public static CartResponseDto of(Cart cart) {
        List<CartItemResponseDto> items = cart.getCartItems().stream()
                .map(CartItemResponseDto::from)
                .toList();

        double subPrice = items.stream()
                .mapToDouble(CartItemResponseDto::getTotalPrice)
                .sum();

        return CartResponseDto.builder()
                .storeId(cart.getCartItems().get(0).getMenu().getStore().getStoreId())
                .content(items)
                .subPrice(subPrice)
                .deliveryFee(cart.getDeliveryFee())
                .storeTotalPrice(subPrice + cart.getDeliveryFee())
                .build();
    }
}
