package com.example.eat_together.domain.chat.entity;

import com.example.eat_together.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

//수정예정_대략적인 틀입니다.
@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_rooms")
public class ChatRoom extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private ChatGroup chatGroup;

    @Column(name = "is_deleted")
    Boolean isDeleted;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomUser> chatRoomUserList= new ArrayList<>();

    public static ChatRoom of(ChatGroup chatGroup){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.chatGroup = chatGroup;
        chatRoom.isDeleted = false;

        return chatRoom;
    }
}
