package com.example.eat_together.domain.order.dto.response;

import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.order.orderEnum.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponseDto {

    private final Long id;

    private final Long userId;

    private final String storeName;

    private final OrderStatus status;

    private final double deliveryFee;

    private final double totalPrice;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    @QueryProjection
    public OrderResponseDto(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getUserId();
        this.storeName = order.getStore().getName();
        this.status = order.getStatus();
        this.deliveryFee = order.getStore().getDeliveryFee();
        this.totalPrice = order.getTotalPrice();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
    }
}
