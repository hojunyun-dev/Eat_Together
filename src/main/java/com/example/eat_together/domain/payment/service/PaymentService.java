package com.example.eat_together.domain.payment.service;

import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.order.repository.OrderRepository;
import com.example.eat_together.domain.payment.dto.response.PaymentResponseDto;
import com.example.eat_together.domain.payment.entity.Payment;
import com.example.eat_together.domain.payment.paymentEnum.PaymentStatus;
import com.example.eat_together.domain.payment.repository.PaymentRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentResponseDto confirmPayment(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        // 본인 결제 맞는지 확인
        if (!payment.getOrder().getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.PAYMENT_FORBIDDEN);
        }

        // 이미 결제된 건지 확인
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new CustomException(ErrorCode.ALREADY_PAID);
        }

        // 결제 확정 처리
        payment.confirm();
        paymentRepository.save(payment);

        Order order = payment.getOrder();

        return new PaymentResponseDto(order.getId(), order.getTotalPrice(), payment.getStatus(), payment.getCreatedAt(), payment.getUpdatedAt());
    }
}