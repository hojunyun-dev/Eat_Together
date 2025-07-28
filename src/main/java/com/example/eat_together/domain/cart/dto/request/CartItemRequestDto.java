package com.example.eat_together.domain.cart.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 장바구니에 담을 메뉴 항목 요청 정보를 담는 DTO
 */
@Getter
@Setter
public class CartItemRequestDto {

    /**
     * 메뉴 ID
     * - 필수 값
     */
    @NotNull(message = "메뉴 ID는 필수입니다.")
    private Long menuId;

    /**
     * 수량
     * - 최소 1 이상, 최대 99 이하
     */
    @Min(value = 1, message = "수량은 최소 1 이상이어야 합니다.")
    @Max(value = 99, message = "수량은 최대 99개까지 담을 수 있습니다.")
    private int quantity;
}
