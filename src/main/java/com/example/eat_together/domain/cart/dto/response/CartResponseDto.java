package com.example.eat_together.domain.cart.dto.response;

import com.example.eat_together.domain.cart.entity.Cart;
import com.example.eat_together.domain.cart.entity.CartItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CartResponseDto {

    private Long storeId;
    private List<CartItemResponseDto> content;
    private double subPrice;         // 메뉴 총합
    private double deliveryFee;      // 배달팁 (개인 주문이면 전체, 공유에서 복사 시 개인분담)
    private double storeTotalPrice;  // 전체 금액 = 메뉴 합계 + 배달팁

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
