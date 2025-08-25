package com.example.eat_together.global.config.kafka;

import com.example.eat_together.domain.notification.messaging.NotificationEnvelope;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import java.util.HashMap;
import java.util.Map;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    //kafka에 메세지 수신 위한 consumer 객체
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> map = new HashMap<>();
        map.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092,localhost:19093,localhost:19094");
        map.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
        map.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(map);
    }

    // 여러 스레드에서 동시에 kafka 메세지 처리 가능
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
    @Bean //알림 전용 ConsumerFactory
    public ConsumerFactory<String, NotificationEnvelope> notificationConsumerFactory() {
        Map<String, Object> map = new HashMap<>();
        map.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092,localhost:19093,localhost:19094");
        map.put(ConsumerConfig.GROUP_ID_CONFIG, "notifications-group"); //알림 전용 그룹ID
        map.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Value를 JSON으로 역직렬화
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        //보안/호환 설정(버전 독립적으로 동작하는 프로퍼티 방식)
        //- TRUSTED_PACKAGES: 역직렬화 가능한 패키지를 제한(보안/성능)
        //- USE_TYPE_INFO_HEADERS: Kafka 헤더의 타입정보 미사용
        //- VALUE_DEFAULT_TYPE: 기본 역직렬화 타입을 NotificationEnvelope로 고정
        map.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.eat_together.*");
        map.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        map.put(JsonDeserializer.VALUE_DEFAULT_TYPE,
                "com.example.eat_together.domain.notification.messaging.NotificationEnvelope");

        return new DefaultKafkaConsumerFactory<>(map);
    }

    @Bean //알림 전용 Listener Container Factory
    public ConcurrentKafkaListenerContainerFactory<String, NotificationEnvelope> notificationKafkaListenerContainerFactory() {
        //알림 리스너(@KafkaListener)에서 containerFactory="notificationKafkaListenerContainerFactory"로 지정해 사용
        ConcurrentKafkaListenerContainerFactory<String, NotificationEnvelope> f =
                new ConcurrentKafkaListenerContainerFactory<>();
        f.setConsumerFactory(notificationConsumerFactory());
        return f;
    }
}
