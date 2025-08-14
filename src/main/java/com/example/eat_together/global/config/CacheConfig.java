package com.example.eat_together.global.config;

import com.example.eat_together.domain.chat.dto.ChatMessageResponseDto;
import com.example.eat_together.domain.menu.dto.respones.MenuResponseDto;
import com.example.eat_together.domain.menu.dto.respones.PagingMenuResponseDto;
import com.example.eat_together.domain.order.dto.response.OrderDetailResponseDto;
import com.example.eat_together.domain.store.dto.response.PagingStoreResponseDto;
import com.example.eat_together.domain.store.dto.response.StoreResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
public class CacheConfig {


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule()) // Java Time 모듈 설정으로 LocalDateTime 직렬화
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // TimeStamp를 문자열로 저장
    }

    private <T>RedisTemplate<String, T> genericRedisTemplate(RedisConnectionFactory connectionFactory, Class<T> clazz) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<T> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper(), clazz);

        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    // 메뉴 단건 조회 Redis 설정
    @Bean
    public RedisTemplate<String, MenuResponseDto> menuRedisTemplate(RedisConnectionFactory connectionFactory) {

        return genericRedisTemplate(connectionFactory, MenuResponseDto.class);
    }

    // 매장 메뉴 목록 조회 Redis 설정
    @Bean
    public RedisTemplate<String, PagingMenuResponseDto> pagingMenuRedisTemplate(RedisConnectionFactory connectionFactory) {

        return genericRedisTemplate(connectionFactory, PagingMenuResponseDto.class);
    }

    // 매장 단건 조회 Redis 설정
    @Bean
    public RedisTemplate<String, StoreResponseDto> storeRedisTemplate(RedisConnectionFactory connectionFactory) {

        return genericRedisTemplate(connectionFactory, StoreResponseDto.class);
    }

    // 매장 목록 조회 Redis 설정
    @Bean
    public RedisTemplate<String, PagingStoreResponseDto> pagingStoreRedisTemplate(RedisConnectionFactory connectionFactory) {

        return genericRedisTemplate(connectionFactory, PagingStoreResponseDto.class);
    }

    // 주문 단건 조회 Redis 설정
    @Bean
    public RedisTemplate<String, OrderDetailResponseDto> orderDetailResponseDtoRedisTemplate(RedisConnectionFactory connectionFactory) {

        return genericRedisTemplate(connectionFactory, OrderDetailResponseDto.class);
    }

    // 채팅 조회 Redis 설정
    @Bean
    public RedisTemplate<String, ChatMessageResponseDto> chatMessageResponseDtoRedisTemplate(RedisConnectionFactory connectionFactory) {

        return genericRedisTemplate(connectionFactory, ChatMessageResponseDto.class);
    }
}
