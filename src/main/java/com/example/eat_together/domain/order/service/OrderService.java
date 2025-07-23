package com.example.eat_together.domain.order.service;

import com.example.eat_together.domain.cart.entity.Cart;
import com.example.eat_together.domain.cart.repository.CartRepository;
import com.example.eat_together.domain.order.dto.OrderResponseDto;
import com.example.eat_together.domain.order.repository.OrderRepository;
import com.example.eat_together.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    // 주문 생성
    @Transactional
    public void createOrder(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        // 장바구니 api 확인 후 수정 예정
//        User user = cart.getUser();
//
//        Order order = Order.of(user);
//
//        for (CartItem cartItem : cart.getCartItems()) {
//            OrderItem orderItem = OrderItem.of(order, cartItem.getMenu(), cartItem.getQuantity(), cartItem.getPrice());
//            order.addOrderItem(orderItem);
//        }

//        order.calculateTotalPrice();
//
//        orderRepository.save(order);

    }

    public Page<OrderResponseDto> getOrders(Long userId, int page, int size) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Pageable pageable = PageRequest.of(page - 1, size);
        return orderRepository.findOrders(pageable);
    }
}
