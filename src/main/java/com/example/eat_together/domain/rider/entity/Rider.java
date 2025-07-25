package com.example.eat_together.domain.rider.entity;

import com.example.eat_together.domain.rider.riderEnum.RiderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.eat_together.domain.user.entity.User;//import문 추가


@Entity
@Getter
@NoArgsConstructor
@Table(name = "riders")
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //유저 연관관계 추가
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiderStatus status = RiderStatus.AVAILABLE; //라이더 배차 가능 여부

    @Column(nullable = false)
    private boolean isDeleted = false; //소프트딜리트

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true; // 추가

    public static Rider of(User user, String phone) {
        Rider rider = new Rider();
        rider.user = user;
        rider.name = user.getName();
        rider.phone = phone;
        rider.isAvailable = true; //
        rider.status = RiderStatus.AVAILABLE;//추가
        return rider;
    }

    //라이더 정보 수정
    public void update(String phone) {
        this.phone = phone;
    }

    //라이더 삭제
    public void delete() {
        this.isDeleted = true;
    }

    //배차 가능 여부(상태 변경)
    public void changeStatus(RiderStatus status) {
        this.status = status;
    }
}
