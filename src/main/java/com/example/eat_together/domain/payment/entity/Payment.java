package com.example.eat_together.domain.payment.entity;

import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.payment.paymentEnum.PaymentStatus;
import com.example.eat_together.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "payments")
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private double amount;

    public static Payment of(Order order) {
        Payment payment = new Payment();
        payment.order = order;
        payment.status = PaymentStatus.PENDING;
        payment.amount = order.getTotalPrice();
        return payment;
    }

    public void confirm() {
        this.status = PaymentStatus.SUCCESS;
    }
}
