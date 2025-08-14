package com.example.eat_together.domain.notification.messaging;

import com.example.eat_together.domain.notification.dto.NotificationResponseDto;

/*Kafka 메시지를 userId + payload 형태로 감싸(NotificationEnvelope) 보내고
Consumer가 userId를 꺼내서 STOMP 목적지(/sub/notifications/{userId})를 만든뒤 payload를 해당 유저에게 실시간 전송한다.*/
public record NotificationEnvelope(Long userId, NotificationResponseDto payload) { }

