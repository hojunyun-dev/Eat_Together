package com.example.eat_together.domain.order.controller;

import com.example.eat_together.domain.order.dto.OrderDetailResponseDto;
import com.example.eat_together.domain.order.dto.OrderResponseDto;
import com.example.eat_together.domain.order.dto.OrderStatusUpdateResponseDto;
import com.example.eat_together.domain.order.orderEnum.OrderResponse;
import com.example.eat_together.domain.order.orderEnum.OrderStatus;
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
                .body(ApiResponse.of(null, OrderResponse.ORDER_CREATED.getMessage()));
    }

    // 주문 목록 페이징 조회 (추후 조회기간, 주문상태로 조회 추가 예정)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponseDto>>> getOrders(@AuthenticationPrincipal UserDetails userDetails,
                                                                         @RequestParam(value = "page", defaultValue = "1") int page,
                                                                         @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(orderService.getOrders(Long.valueOf(userDetails.getUsername()), page, size), OrderResponse.ORDER_LIST_FOUND.getMessage()));
    }

    // 주문 목록 단일 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponseDto>> getOrder(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(orderService.getOrder(Long.valueOf(userDetails.getUsername()), orderId), OrderResponse.ORDER_FOUND.getMessage()));
    }

    // 주문 상태 변경(가게 권한)
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderStatusUpdateResponseDto>> updateOrderStatus(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long orderId, @RequestBody OrderStatus status) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(orderService.updateOrderStatus(Long.valueOf(userDetails.getUsername()), orderId, status), OrderResponse.ORDER_UPDATED.getMessage()));
    }

    // 주문 단건 삭제 (소프트 딜리트)
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long orderId) {
        orderService.deleteOrder(Long.valueOf(userDetails.getUsername()), orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(null, OrderResponse.ORDER_DELETED.getMessage()));
    }

}
