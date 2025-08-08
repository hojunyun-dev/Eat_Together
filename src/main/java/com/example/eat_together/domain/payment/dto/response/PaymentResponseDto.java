package com.example.eat_together.domain.payment.dto.response;

import com.example.eat_together.domain.payment.paymentEnum.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PaymentResponseDto {
    private Long orderId;
    private double totalPrice;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaymentResponseDto(Long orderId, double totalPrice, PaymentStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
