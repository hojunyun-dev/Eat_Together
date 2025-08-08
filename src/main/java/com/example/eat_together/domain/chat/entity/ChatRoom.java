package com.example.eat_together.domain.chat.entity;

import com.example.eat_together.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_rooms")
public class ChatRoom extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToOne
    @JoinColumn(name = "group_id")
    private ChatGroup chatGroup;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomUser> chatRoomUserList = new ArrayList<>();

    private Long currentMemberCount;

    public static ChatRoom of(ChatGroup chatGroup) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.chatGroup = chatGroup;
        chatRoom.isDeleted = false;
        chatRoom.currentMemberCount = 1L;

        return chatRoom;
    }

    public void updateCount(Long currentMemberCount){
        this.currentMemberCount = currentMemberCount;
    }
}
