package com.example.eat_together.domain.cart.dto.response;

import lombok.Getter;

import java.util.List;

/**
 * 장바구니 전체 응답 정보를 담는 DTO
 */
@Getter
public class CartResponseDto {

    private final Long storeId;
    private final List<CartItemResponseDto> content;
    private final double subPrice;
    private final double deliveryTip;
    private final double storeTotalPrice;

    /**
     * 장바구니 응답 객체 생성자
     *
     * @param storeId     매장 ID
     * @param content     장바구니 항목 리스트
     * @param deliveryTip 배달팁
     */
    public CartResponseDto(Long storeId, List<CartItemResponseDto> content, double deliveryTip) {
        this.storeId = storeId;
        this.content = content;
        this.subPrice = content.stream().mapToDouble(CartItemResponseDto::getTotalPrice).sum();
        this.deliveryTip = deliveryTip;
        this.storeTotalPrice = subPrice + deliveryTip;
    }
}
