package com.example.eat_together.domain.order.dto;

import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.order.entity.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponseDto {

    private final Long id;

    private final Long userId;

    private final OrderStatus status;

    private final double totalPrice;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    @QueryProjection
    public OrderResponseDto(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getUserId();
        this.status = order.getStatus();
        this.totalPrice = order.getTotalPrice();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
    }

}
