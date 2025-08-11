package com.example.eat_together.domain.payment.paymentEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentResponse {
    PAYMENT_CONFIRM("결제가 완료되었습니다.");

    private final String message;
}
