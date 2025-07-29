package com.example.eat_together.domain.store.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class StoreUpdateRequestDto {

    private String name;

    private String description;

    private String address;

    private LocalTime openTime;

    private LocalTime closeTime;

    private Double deliveryFee;

    private String category;

    private String phoneNumber;

    public StoreUpdateRequestDto(String name,
                                 String description,
                                 String address,
                                 LocalTime openTime,
                                 LocalTime closeTime,
                                 Double deliveryFee,
                                 String category,
                                 String phoneNumber) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.deliveryFee = deliveryFee;
        this.category = category;
        this.phoneNumber = phoneNumber;
    }
}
