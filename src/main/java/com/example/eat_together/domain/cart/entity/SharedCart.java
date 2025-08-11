package com.example.eat_together.domain.cart.entity;

import com.example.eat_together.domain.chat.entity.ChatRoom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class SharedCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "sharedCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SharedCartItem> items = new ArrayList<>();

    private SharedCart(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public static SharedCart of(ChatRoom chatRoom) {
        return new SharedCart(chatRoom);
    }

    public void addItem(SharedCartItem item) {
        items.add(item);
        item.setSharedCart(this);
    }
}
