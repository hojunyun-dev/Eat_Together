package com.example.eat_together.domain.store.dto.response;

import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.entity.category.FoodCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class StoreResponseDto {

    private Long storeId;
    private String name;
    private String description;
    private String address;
    @JsonProperty("open")
    private boolean isOpen;
    private LocalTime openTime;
    private LocalTime closeTime;
    private FoodCategory category;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    public static StoreResponseDto from(Store store) {
        return new StoreResponseDto(
                store.getStoreId(),
                store.getName(),
                store.getDescription(),
                store.getAddress(),
                store.isOpen(),
                store.getOpenTime(),
                store.getCloseTime(),
                store.getFoodCategory(),
                store.getPhoneNumber(),
                store.getCreatedAt(),
                store.getUpdatedAt()
        );
    }

}
