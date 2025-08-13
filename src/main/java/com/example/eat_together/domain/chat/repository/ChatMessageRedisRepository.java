package com.example.eat_together.domain.chat.repository;

import com.example.eat_together.domain.chat.dto.ChatMessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageRedisRepository {

    private final RedisTemplate<String, ChatMessageResponseDto> chatMessageResponseDtoRedisTemplate;

    public void saveChatMessage(List<ChatMessageResponseDto> chatMessageResponseDtoList, String CHAT_CACHE_KEY, Duration TTL) {

        chatMessageResponseDtoRedisTemplate.delete(CHAT_CACHE_KEY);
        chatMessageResponseDtoRedisTemplate.opsForList().rightPushAll(CHAT_CACHE_KEY, chatMessageResponseDtoList);
        chatMessageResponseDtoRedisTemplate.expire(CHAT_CACHE_KEY, TTL);

    }
}
