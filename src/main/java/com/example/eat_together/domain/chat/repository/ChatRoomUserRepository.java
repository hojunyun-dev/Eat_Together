package com.example.eat_together.domain.chat.repository;

import com.example.eat_together.domain.chat.entity.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    Long countByChatRoomId(Long chatRoomId);

    @Modifying
    @Query("""
                DELETE
                FROM ChatRoomUser  cru
                WHERE cru.user.userId = :userid AND cru.chatRoom.id = :roomid
            """)
    void deleteByUserIdAndRoomId(@Param("userid") Long userId, @Param("roomid") Long roomId);
}
