package com.example.eat_together.domain.chat.repository;

import com.example.eat_together.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
        SELECT DISTINCT cr
        FROM ChatRoom cr
        JOIN FETCH cr.chatGroup cg
        LEFT JOIN FETCH cr.chatRoomUserList cl
    """)
    List<ChatRoom> findAll();
}