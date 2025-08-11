package com.example.eat_together.domain.payment.paymentEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus {
    PENDING("결제대기"),
    SUCCESS("결제성공"),
    FAILURE("결제실패");

    private final String message;
}
