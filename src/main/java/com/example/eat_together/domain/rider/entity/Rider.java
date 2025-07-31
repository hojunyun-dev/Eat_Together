package com.example.eat_together.domain.rider.entity;

import com.example.eat_together.domain.rider.riderEnum.RiderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.eat_together.domain.users.common.entity.User;//import문 추가

import java.time.LocalTime;


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
    private boolean isAvailable = true;

    @Column(name = "open_time") // 라이더 영업 시작시간 추가
    private LocalTime openTime;

    @Column(name = "close_time") // 라이더 영업 종료시간 추가
    private LocalTime closeTime;


    public static Rider of(User user, String phone, LocalTime openTime, LocalTime closeTime) {
        Rider rider = new Rider();
        rider.user = user;
        rider.name = user.getName();
        rider.phone = phone;
        rider.openTime = openTime; //필드 추가
        rider.closeTime = closeTime; //추가
        rider.isAvailable = true;
        rider.status = RiderStatus.AVAILABLE;
        return rider;
    }

    //라이더 정보 수정
    public void update(String phone, LocalTime openTime, LocalTime closeTime) {
        this.phone = phone;
        this.openTime = openTime; //새로 반영
        this.closeTime = closeTime;//
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
