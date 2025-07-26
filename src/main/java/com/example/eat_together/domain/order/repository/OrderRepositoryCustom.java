package com.example.eat_together.domain.order.repository;

import com.example.eat_together.domain.order.dto.response.OrderResponseDto;
import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.order.orderEnum.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepositoryCustom {
    Page<OrderResponseDto> findOrdersByUserId(Long userId, Pageable pageable, LocalDate startDate, LocalDate endDate, OrderStatus status);

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    List<Order> findByUserIdAndStoreIdAndStatus(Long userId, Long storeId, OrderStatus status);
}
