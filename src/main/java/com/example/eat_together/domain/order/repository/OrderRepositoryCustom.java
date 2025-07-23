package com.example.eat_together.domain.order.repository;

import com.example.eat_together.domain.order.dto.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepositoryCustom {
    Page<OrderResponseDto> findOrders(Pageable pageable);
}
