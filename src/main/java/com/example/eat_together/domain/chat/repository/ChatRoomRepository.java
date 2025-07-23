package com.example.eat_together.domain.chat.repository;

import com.example.eat_together.domain.chat.entity.ChatMessage;
import com.example.eat_together.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
