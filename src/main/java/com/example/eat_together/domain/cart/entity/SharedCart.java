package com.example.eat_together.domain.cart.entity;

import com.example.eat_together.domain.chat.entity.ChatRoom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 채팅방 기반 공유 장바구니 엔티티
 */
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

    /**
     * SharedCart 인스턴스 생성
     *
     * @param chatRoom 공유 장바구니가 속한 채팅방
     * @return 생성된 SharedCart 인스턴스
     */
    public static SharedCart of(ChatRoom chatRoom) {
        return new SharedCart(chatRoom);
    }

    /**
     * 공유 장바구니에 항목 추가
     *
     * @param item 추가할 항목
     */
    public void addItem(SharedCartItem item) {
        items.add(item);
        item.setSharedCart(this);
    }
}