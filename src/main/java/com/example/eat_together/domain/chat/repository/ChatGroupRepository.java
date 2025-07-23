package com.example.eat_together.domain.chat.repository;

import com.example.eat_together.domain.chat.entity.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
}
