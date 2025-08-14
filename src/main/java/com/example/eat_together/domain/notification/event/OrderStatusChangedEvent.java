package com.example.eat_together.domain.notification.event;

// 주문 상태 변경 시 알림
public record OrderStatusChangedEvent(Long userId, Long orderId, String from, String to) { }

