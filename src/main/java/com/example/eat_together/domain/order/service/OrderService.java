package com.example.eat_together.domain.order.service;

import com.example.eat_together.domain.cart.entity.Cart;
import com.example.eat_together.domain.cart.entity.CartItem;
import com.example.eat_together.domain.cart.repository.CartRepository;
import com.example.eat_together.domain.order.dto.OrderDetailResponseDto;
import com.example.eat_together.domain.order.dto.OrderResponseDto;
import com.example.eat_together.domain.order.dto.OrderStatusUpdateResponseDto;
import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.order.entity.OrderItem;
import com.example.eat_together.domain.order.orderEnum.OrderStatus;
import com.example.eat_together.domain.order.repository.OrderRepository;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.entity.UserRole;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // 주문 생성
    @Transactional
    public void createOrder(Long userId) {

        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        User user = cart.getUser();

        if (cart.getCartItems().isEmpty()) {
            throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        Store store = cart.getCartItems().get(0).getMenu().getStore();

        Order order = Order.of(user, store);

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = OrderItem.of(order, cartItem.getMenu(), cartItem.getQuantity());
            order.addOrderItem(orderItem);
        }

        order.calculateTotalPrice();

        orderRepository.save(order);

    }

    // 주문 목록 조회
    public Page<OrderResponseDto> getOrders(Long userId, int page, int size, LocalDate startDate, LocalDate endDate, OrderStatus status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return orderRepository.findOrdersByUserId(userId, pageable, startDate, endDate, status);
    }

    // 주문 목록 단일 조회
    public OrderDetailResponseDto getOrder(Long userId, Long orderId) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Order order = orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        return new OrderDetailResponseDto(order);
    }

    // 주문 상태 변경(가게 권한)
    @Transactional
    public OrderStatusUpdateResponseDto updateOrderStatus(Long userId, Long orderId, OrderStatus status) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException((ErrorCode.USER_NOT_FOUND)));

        if (user.getRole() != UserRole.OWNER) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        order.updateStatus(status);

        orderRepository.save(order);

        return new OrderStatusUpdateResponseDto(order);
    }

    // 주문 단건 삭제 (소프트 딜리트)
    @Transactional
    public void deleteOrder(Long userId, Long orderId) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Order order = orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        order.deletedOrder();
    }
}
