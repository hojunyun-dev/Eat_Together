package com.example.eat_together.domain.rider.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "riders")
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    private String phone;

    @Column(nullable = false)
    private boolean isAvailable = true; //라이더 배차 가능 여부

    @Column(nullable = false)
    private boolean isDeleted = false; //소프트딜리트

    //라이더 생성
    public static Rider of(String name, String phone) {
        Rider rider = new Rider();
        rider.name = name;
        rider.phone = phone;
        return rider;
    }

    //라이더 정보 수정
    public void update(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    //라이더 삭제
    public void softDelete() {
        this.isDeleted = true;
    }

    //배차 가능 여부(상태 변경)
    public void changeAvailability(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
