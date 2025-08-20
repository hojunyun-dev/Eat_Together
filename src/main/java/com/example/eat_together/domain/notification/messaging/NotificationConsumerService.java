package com.example.eat_together.domain.notification.messaging;

import com.example.eat_together.domain.notification.dto.NotificationResponseDto;
import com.example.eat_together.domain.notification.messaging.NotificationEnvelope;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

/**
 * 카프카에서 알림을 받아서 STOMP로 유저에게 전송
 * - 목적지는 /sub/notifications/{userId} 로 통일
 *   -이유: 통일하면 서버가 특정 유저에게 알림을 보낼때 경로만 userID에 맞춰서 전송됩니다.
 */
@Component
@RequiredArgsConstructor
public class NotificationConsumerService {
    private static final String TOPIC = "notifications";
    private final SimpMessageSendingOperations messaging;
    private final ObjectMapper objectMapper;

    //해당 메서드는 카프카에서 메시지를 받아서 실시간 알림을 해당 유저에게 보내는 역할
    @KafkaListener(topics = TOPIC)
    public void listen(String message) {
        try {
            NotificationEnvelope env = objectMapper.readValue(message, NotificationEnvelope.class);
            Long userId = env.userId();
            NotificationResponseDto dto = env.payload();

            messaging.convertAndSend("/sub/notifications/" + userId, dto);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CANT_CONSUME_MESSAGE);
        }
    }
}
