package com.example.eat_together.domain.cart.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공유 장바구니에 담을 메뉴 항목 요청 정보를 담는 DTO
 */
@Getter
@NoArgsConstructor
public class SharedCartItemRequestDto {

    /**
     * 메뉴 ID
     * - 필수 값
     */
    @NotNull(message = "메뉴 ID는 필수")
    private Long menuId;

    /**
     * 수량
     * - 최소 1 이상, 최대 99 이하
     */
    @Min(value = 1, message = "수량은 최소 1 이상")
    @Max(value = 99, message = "수량은 최대 99까지 허용")
    private int quantity;
}
