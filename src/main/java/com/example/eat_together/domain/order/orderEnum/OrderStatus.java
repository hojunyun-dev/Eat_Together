package com.example.eat_together.domain.order.orderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {
    ORDERED("주문완료"),
    DELIVERING("배달중"),
    DELIVERED("배달완료");

    private final String message;
}
