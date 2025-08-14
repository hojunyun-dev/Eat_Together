package com.example.eat_together.domain.payment.service;

import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.payment.dto.response.PaymentResponseDto;
import com.example.eat_together.domain.payment.entity.Payment;
import com.example.eat_together.domain.payment.paymentEnum.PaymentStatus;
import com.example.eat_together.domain.payment.repository.PaymentRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.redis.service.LockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LockService lockService;

    @Transactional
    public PaymentResponseDto confirmPayment(Long userId, Long paymentId) {
        // Runnable은 반환값이 없으므로 배열을 이용해 락 안에서 만든 DTO를 밖으로 전달
        PaymentResponseDto[] result = new PaymentResponseDto[1];

        lockService.executeWithLockForPayment(paymentId, () -> {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

            // 본인 결제 맞는지 확인
            if (!payment.getOrder().getUser().getUserId().equals(userId)) {
                throw new CustomException(ErrorCode.PAYMENT_FORBIDDEN);
            }

            // 이미 확정된 결제인지 확인
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                throw new CustomException(ErrorCode.ALREADY_PAID);
            }

            // 결제 확정 처리
            payment.confirm();
            paymentRepository.save(payment);

            Order order = payment.getOrder();
            result[0] = new PaymentResponseDto(order.getId(), order.getTotalPrice(),
                    payment.getStatus(), payment.getCreatedAt(), payment.getUpdatedAt());
        });
        return result[0];
    }
}