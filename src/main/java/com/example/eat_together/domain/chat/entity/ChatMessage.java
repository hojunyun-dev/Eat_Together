package com.example.eat_together.domain.chat.entity;

import com.example.eat_together.domain.chat.dto.ChatMessageRequestDto;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

//수정예정_대략적인 틀입니다.
@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_messages")
public class ChatMessage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String message;

    public static ChatMessage of(ChatMessageRequestDto chatMessageRequestDto, User user, ChatRoom chatRoom){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.message = chatMessageRequestDto.getMessage();
        chatMessage.user = user;
        chatMessage.chatRoom = chatRoom;

        return chatMessage;
    }
    //test
    public static ChatMessage of(ChatMessageRequestDto chatMessageRequestDto, ChatRoom chatRoom){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.message = chatMessageRequestDto.getMessage();
        chatMessage.chatRoom = chatRoom;

        return chatMessage;
    }
}
