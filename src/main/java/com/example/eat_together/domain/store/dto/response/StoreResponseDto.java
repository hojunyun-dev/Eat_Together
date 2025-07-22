package com.example.eat_together.domain.store.dto.response;

import com.example.eat_together.domain.store.entity.category.FoodCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class StoreResponseDto {

    private final Long storeId;
    private final String name;
    private final String description;
    private final String address;
    private final boolean isOpen;
    private final LocalTime openTime;
    private final LocalTime closeTime;
    private final FoodCategory category;
    private final String phoneNumber;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public StoreResponseDto(Long storeId,
                            String name,
                            String description,
                            String address,
                            boolean isOpen,
                            LocalTime openTime,
                            LocalTime closeTime,
                            FoodCategory category,
                            String phoneNumber,
                            LocalDateTime createdAt,
                            LocalDateTime updatedAt
    ) {
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.address = address;
        this.isOpen = isOpen;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.category = category;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
