package com.example.eat_together.domain.order.controller;

import com.example.eat_together.domain.order.dto.OrderDetailResponseDto;
import com.example.eat_together.domain.order.dto.OrderResponseDto;
import com.example.eat_together.domain.order.service.OrderService;
import com.example.eat_together.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createOrder(@AuthenticationPrincipal UserDetails userDetails) {

        orderService.createOrder(Long.valueOf(userDetails.getUsername()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(null, "주문이 완료되었습니다."));
    }

    // 주문 목록 페이징 조회 (추후 조회기간, 주문상태로 조회 추가 예정)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponseDto>>> getOrders(@AuthenticationPrincipal UserDetails userDetails,
                                                                         @RequestParam(value = "page", defaultValue = "1") int page,
                                                                         @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(orderService.getOrders(Long.valueOf(userDetails.getUsername()), page, size), "주문 목록을 조회하였습니다."));
    }

    // 주문 목록 단일 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponseDto>> getOrder(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(orderService.getOrder(Long.valueOf(userDetails.getUsername()), orderId), "주문 목록을 조회하였습니다."));
    }
}
