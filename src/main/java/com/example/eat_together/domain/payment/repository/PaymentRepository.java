package com.example.eat_together.domain.payment.repository;

import com.example.eat_together.domain.payment.entity.Payment;
import com.example.eat_together.domain.payment.paymentEnum.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByIdAndStatus(Long paymentId, PaymentStatus paymentStatus);
}
