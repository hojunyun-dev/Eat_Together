package com.example.eat_together.domain.chat.entity;

import com.example.eat_together.domain.chat.chatEnum.MemberRole;
import com.example.eat_together.domain.users.common.entity.User;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role")
    private MemberRole memberRole;

    @CreatedDate
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    public static ChatRoomUser of(ChatRoom chatRoom, User user, MemberRole memberRole) {
        ChatRoomUser chatRoomUser = new ChatRoomUser();
        chatRoomUser.chatRoom = chatRoom;
        chatRoomUser.user = user;
        chatRoomUser.memberRole = memberRole;

        return chatRoomUser;
    }
}
