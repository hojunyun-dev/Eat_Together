package com.example.eat_together.domain.cart.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 공유 장바구니 응답 정보를 담는 DTO
 */
@Getter
@Builder
public class SharedCartResponseDto {

    private final Long storeId;
    private final double deliveryFee;
    private final List<SharedCartItemResponseDto> items;
}
