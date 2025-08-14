package com.example.eat_together.domain.chat.service;

import com.example.eat_together.domain.chat.dto.ChatMessageResponseDto;
import com.example.eat_together.domain.chat.entity.ChatMessage;
import com.example.eat_together.domain.chat.repository.ChatMessageRedisRepository;
import com.example.eat_together.domain.chat.repository.ChatMessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageRedisService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageRedisRepository chatMessageRedisRepository;
    private final RedisTemplate<String, ChatMessageResponseDto> chatMessageResponseDtoRedisTemplate;
    private static final Duration TTL = Duration.ofDays(1L);

    @Transactional
    public List<ChatMessageResponseDto> getMessageList(Long roomId) {

        String CHAT_CACHE_KEY = "roomId:" + roomId;
        List<ChatMessageResponseDto> cached = chatMessageResponseDtoRedisTemplate.opsForList().range(CHAT_CACHE_KEY, 0, -1);

        if (cached == null || cached.isEmpty()) {
            List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomId(roomId);
            List<ChatMessageResponseDto> chatMessageResponseDtoList = chatMessageList.stream()
                    .map(chatMessage -> ChatMessageResponseDto
                            .of(chatMessage.getUser().getUserId(),
                                    chatMessage.getChatRoom().getId(),
                                    chatMessage.getMessage()
                            )).toList();
            chatMessageRedisRepository.saveChatMessage(chatMessageResponseDtoList, CHAT_CACHE_KEY, TTL);

            return chatMessageResponseDtoList;
        }

        return cached;
    }

}
