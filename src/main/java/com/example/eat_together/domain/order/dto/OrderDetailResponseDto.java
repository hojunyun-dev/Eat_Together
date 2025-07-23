package com.example.eat_together.domain.order.dto;

import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.order.entity.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderDetailResponseDto {
    private final Long id;

    private final Long userId;

    private final Long storeId;

    private final OrderStatus status;

    private final double deliveryFee;

    private final double totalPrice;

    private final List<OrderItemResponseDto> orderItems;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    public OrderDetailResponseDto(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getUserId();
        this.storeId = order.getStore().getStoreId();
        this.status = order.getStatus();
        this.deliveryFee = order.getStore().getDeliveryFee();
        this.totalPrice = order.getTotalPrice();
        this.orderItems = order.getOrderItems().stream()
                .map(item -> new OrderItemResponseDto(
                        item.getMenu().getMenuId(),
                        item.getMenu().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice() * item.getQuantity()
                ))
                .toList();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
    }
}
