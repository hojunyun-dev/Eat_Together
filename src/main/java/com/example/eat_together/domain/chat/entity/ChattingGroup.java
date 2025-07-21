package com.example.eat_together.domain.chat.entity;

import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

//수정예정_대략적인 틀입니다.
@Entity
@Getter
@NoArgsConstructor
@Table(name = "chatting_groups")
public class ChattingGroup extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private User host;

    String description;

    @Column(name = "is_deleted")
    Boolean isDeleted;

    @OneToOne(mappedBy = "chattingGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChatRoom chatRoom;
}
