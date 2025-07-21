package com.example.eat_together.domain.store.entity;

import com.example.eat_together.domain.store.category.FoodCategory;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.global.entity.BaseTimeEntity;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String address;

    @Column(name = "is_open", nullable = false)
    private boolean isOpen;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "delivery_fee", nullable = false)
    private int deliveryFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "food_category", nullable = false)
    private FoodCategory foodCategory;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;


    public static Store of(User user,
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
        store.user = user;
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
