package com.example.eat_together.domain.chat.entity;

import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_room_users")
public class ChatRoomUser extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
/*
    @Column(name = "is_left")
    private boolean isLeft;


 */
    @CreatedDate
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    public static ChatRoomUser of(ChatRoom chatRoom, User user) {
        ChatRoomUser chatRoomUser = new ChatRoomUser();
      //  chatRoomUser.isLeft = false;
        chatRoomUser.chatRoom = chatRoom;
        chatRoomUser.user = user;

        return chatRoomUser;
    }
}
