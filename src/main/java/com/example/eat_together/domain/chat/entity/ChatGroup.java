package com.example.eat_together.domain.chat.entity;

import com.example.eat_together.domain.chat.chatEnum.FoodType;
import com.example.eat_together.domain.chat.chatEnum.Status;
import com.example.eat_together.domain.chat.dto.ChatGroupDto;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_groups")
public class ChatGroup extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;

    private String description;

    @Column(name = "food_type")
    @Enumerated(EnumType.STRING)
    private FoodType foodType;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToOne(mappedBy = "chatGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChatRoom chatRoom;

    private Integer maxMember;

    @Enumerated(EnumType.STRING)
    private Status status;

    public static ChatGroup of(User user, ChatGroupDto chatGroupDto) {
        ChatGroup chatGroup = new ChatGroup();
        chatGroup.title = chatGroupDto.getTitle();
        chatGroup.host = user;
        chatGroup.description = chatGroupDto.getDescription();
        chatGroup.foodType = chatGroupDto.getFoodType();
        chatGroup.isDeleted = false;
        chatGroup.maxMember = chatGroupDto.getMaxMember();
        chatGroup.status = chatGroupDto.getStatus();

        return chatGroup;
    }
}
