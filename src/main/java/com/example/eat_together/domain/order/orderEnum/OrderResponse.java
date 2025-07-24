package com.example.eat_together.domain.order.orderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderResponse {
    ORDER_CREATED("주문이 완료되었습니다."),
    ORDER_LIST_FOUND("주문 목록을 조회하였습니다."),
    ORDER_FOUND("주문을 조회하였습니다."),
    ORDER_UPDATED("주문 상태를 변경하였습니다."),
    ORDER_DELETED("주문정보가 삭제되었습니다.");

    private final String message;
}
