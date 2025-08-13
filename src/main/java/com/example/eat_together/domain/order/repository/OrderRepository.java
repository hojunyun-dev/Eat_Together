package com.example.eat_together.domain.order.repository;

import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.order.orderEnum.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    //라이더와 상태 기반 주문 조회
    List<Order> findByRider_IdAndStatus(Long riderId, OrderStatus status);

    boolean existsByUserUserIdAndStoreStoreIdAndStatus(Long userId, Long storeId, OrderStatus status);
}
