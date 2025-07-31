package com.example.eat_together.domain.store.entity;

import com.example.eat_together.domain.store.entity.category.FoodCategory;
import com.example.eat_together.domain.users.common.entity.User;
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

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "delivery_fee", nullable = false)
    private double deliveryFee;

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
                           double deliveryFee,
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

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void updateOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public void updateCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public void updateDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public void updateFoodCategory(FoodCategory foodCategory) {
        this.foodCategory = foodCategory;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void openStore() {
        this.isOpen = true;
    }

    public void closeStore() {
        this.isOpen = false;
    }

    // 매장 삭제 시 사용
    public void deleted() {
        this.isDeleted = true;
    }

    // 매장 삭제를 되돌릴 때 사용
    public void returnDeleted() {
        this.isDeleted = false;
    }

}
