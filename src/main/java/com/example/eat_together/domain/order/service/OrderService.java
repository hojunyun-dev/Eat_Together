package com.example.eat_together.domain.order.service;

import com.example.eat_together.domain.cart.entity.Cart;
import com.example.eat_together.domain.cart.entity.CartItem;
import com.example.eat_together.domain.cart.repository.CartRepository;
import com.example.eat_together.domain.order.dto.OrderDetailResponseDto;
import com.example.eat_together.domain.order.dto.OrderResponseDto;
import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.order.entity.OrderItem;
import com.example.eat_together.domain.order.repository.OrderRepository;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    // 주문 생성
    @Transactional
    public void createOrder(Long userId) {

        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        User user = cart.getUser();

        Order order = Order.of(user);

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = OrderItem.of(order, cartItem.getMenu(), cartItem.getQuantity());
            order.addOrderItem(orderItem);
        }

        order.calculateTotalPrice();

        orderRepository.save(order);

    }

    // 주문 목록 조회 (추후 조회기간, 주문상태로 조회 추가 예정)
    public Page<OrderResponseDto> getOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return orderRepository.findOrdersByUserId(userId, pageable);
    }

    // 주문 목록 단일 조회
    public OrderDetailResponseDto getOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserUserId(orderId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        return new OrderDetailResponseDto(order);
    }
}
