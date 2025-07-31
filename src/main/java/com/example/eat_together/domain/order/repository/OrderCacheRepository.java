package com.example.eat_together.domain.order.repository;

import com.example.eat_together.domain.order.dto.response.OrderDetailResponseDto;
import com.example.eat_together.domain.order.orderEnum.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class OrderCacheRepository {

    private final RedisTemplate<String, OrderDetailResponseDto> orderRedisTemplate;

    private static final String ORDER_CACHE_KEY = "order:"; // 주문 단일 조회 키

    public OrderDetailResponseDto getOrder(Long userId, Long orderId) {
        return orderRedisTemplate.opsForValue().get(ORDER_CACHE_KEY + userId + ":" + orderId);
    }

    public void saveOrderCache(Long userId, Long orderId, OrderDetailResponseDto orderDto) {
        long TTL;
        if (orderDto.getStatus().equals(OrderStatus.ORDERED) || orderDto.getStatus().equals(OrderStatus.DELIVERING)) {
            TTL = 5;
        } else {
            TTL = 30;
        }
        orderRedisTemplate.opsForValue().set(ORDER_CACHE_KEY + userId + ":" + orderId, orderDto, TTL, TimeUnit.MINUTES);
    }

    public void evictOrder(Long userId, Long orderId) {
        orderRedisTemplate.delete(ORDER_CACHE_KEY + userId + ":" + orderId);
    }
}
