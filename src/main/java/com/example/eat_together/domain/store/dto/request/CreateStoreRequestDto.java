package com.example.eat_together.domain.store.dto.request;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public class CreateStoreRequestDto {

    private final String name;
    private final String description;
    private final String address;
    private final LocalTime openTime;
    private final LocalTime closeTime;
    private final int deliveryFee;
    private final String category;
    private final String phoneNumber;


    public CreateStoreRequestDto(String name,
                                 String description,
                                 String address,
                                 LocalTime openTime,
                                 LocalTime closeTime,
                                 int deliveryFee,
                                 String category,
                                 String phoneNumber
    ) {
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
