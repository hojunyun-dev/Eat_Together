package com.example.eat_together.domain.order.repository;

import com.example.eat_together.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    Optional<Order> findByIdAndUserUserId(Long orderId, Long userId);
}
