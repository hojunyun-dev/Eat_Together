package com.example.eat_together.domain.notification.event;

// 라이더 상태 변경 시 알림
public record RiderStatusChangedEvent(Long userId, Long riderId, String from, String to) { }

