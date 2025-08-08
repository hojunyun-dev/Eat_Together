package com.example.eat_together.domain.payment.controller;

import com.example.eat_together.domain.payment.dto.response.PaymentResponseDto;
import com.example.eat_together.domain.payment.paymentEnum.PaymentResponse;
import com.example.eat_together.domain.payment.service.PaymentService;
import com.example.eat_together.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제
    @PostMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> confirmPayment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long paymentId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(paymentService.confirmPayment(Long.valueOf(userDetails.getUsername()), paymentId), PaymentResponse.PAYMENT_CONFIRM.getMessage()));
    }
}
