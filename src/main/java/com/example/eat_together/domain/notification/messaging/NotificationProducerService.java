package com.example.eat_together.domain.notification.messaging;

import com.example.eat_together.domain.notification.dto.NotificationResponseDto;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * 알림을 카프카로 발행하는 역할.
 * -이유: 다중 서버에서 각 서버가 같은 토픽을 구독해서 자기한테 붙어있는 세션들한테 뿌리기 위해서
 */
@Service
@RequiredArgsConstructor
public class NotificationProducerService {
    private static final String TOPIC = "notifications";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /*해당 메서드는 알림을 Kafka로 발행할 때 같은 유저의 알림이 같은 파티션으로 가도록
    key=userId를 설정해서 메시지 순서를 보장하려는 목적을 가지고 있다.*/
    public void send(Long userId, NotificationResponseDto payload) {
        try {
            String json = objectMapper.writeValueAsString(new NotificationEnvelope(userId, payload));
            kafkaTemplate.send(TOPIC, String.valueOf(userId), json);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CANT_PRODUCE_MESSAGE);
        }
    }
}
