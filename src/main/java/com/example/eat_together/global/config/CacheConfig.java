package com.example.eat_together.global.config;

import com.example.eat_together.domain.menu.dto.respones.MenuResponseDto;
import com.example.eat_together.domain.menu.dto.respones.PagingMenuResponseDto;
import com.example.eat_together.domain.store.dto.response.PagingStoreResponseDto;
import com.example.eat_together.domain.store.dto.response.StoreResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule()) // Java Time 모듈 설정으로 LocalDateTime 직렬화
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // TimeStamp를 문자열로 저장
    }

    // 메뉴 단건 조회 Redis 설정
    @Bean
    public RedisTemplate<String, MenuResponseDto> menuRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, MenuResponseDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<MenuResponseDto> serializer =
                new Jackson2JsonRedisSerializer<>(MenuResponseDto.class);

        serializer.setObjectMapper(objectMapper());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    // 매장 메뉴 목록 조회 Redis 설정
    @Bean
    public RedisTemplate<String, PagingMenuResponseDto> pagingMenuRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, PagingMenuResponseDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<PagingMenuResponseDto> serializer =
                new Jackson2JsonRedisSerializer<>(PagingMenuResponseDto.class);

        serializer.setObjectMapper(objectMapper());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    // 매장 단건 조회 Redis 설정
    @Bean
    public RedisTemplate<String, StoreResponseDto> storeRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, StoreResponseDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<StoreResponseDto> serializer =
                new Jackson2JsonRedisSerializer<>(StoreResponseDto.class);

        serializer.setObjectMapper(objectMapper());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    // 매장 목록 조회 Redis 설정
    @Bean
    public RedisTemplate<String, PagingStoreResponseDto> pagingStoreRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, PagingStoreResponseDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<PagingStoreResponseDto> serializer =
                new Jackson2JsonRedisSerializer<>(PagingStoreResponseDto.class);

        serializer.setObjectMapper(objectMapper());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
}
