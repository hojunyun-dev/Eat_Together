package com.example.eat_together.store.entity;

import com.example.eat_together.global.entity.BaseTimeEntity;
import com.example.eat_together.store.category.FoodCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@Table(name = "stores")
@NoArgsConstructor
public class Store extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    // 유저 부분 병합 시 수정 예정
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User userId;

    // 임시 유저    <-------------- 삭제 예정
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private boolean isOpen;

    private LocalTime openTime;

    private LocalTime closeTime;

    @Column(nullable = false)
    private int deliveryFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodCategory foodCategory;

    @Column(nullable = false)
    private String phoneNumber;


    // 유저 부분 병합 시 수정 필요
    public static Store of(//User
                           Long userId,
                           String name,
                           String description,
                           String address,
                           boolean isOpen,
                           LocalTime openTime,
                           LocalTime closeTime,
                           int deliveryFee,
                           FoodCategory foodCategory,
                           String phoneNumber
    ) {
        Store store = new Store();
        store.userId = userId;
        store.name = name;
        store.description = description;
        store.address = address;
        store.isOpen = isOpen;
        store.openTime = openTime;
        store.closeTime = closeTime;
        store.deliveryFee = deliveryFee;
        store.foodCategory = foodCategory;
        store.phoneNumber = phoneNumber;
        return store;
    }
}
