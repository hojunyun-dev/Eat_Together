package com.example.eat_together.domain.order.dto;

import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.order.orderEnum.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderStatusUpdateResponseDto {

    private final Long id;

    private final Long userId;

    private final Long storeId;

    private final OrderStatus status;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    public OrderStatusUpdateResponseDto(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getUserId();
        this.storeId = order.getStore().getStoreId();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
    }
}
