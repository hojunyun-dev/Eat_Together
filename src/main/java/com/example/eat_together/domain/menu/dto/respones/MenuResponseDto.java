package com.example.eat_together.domain.menu.dto.respones;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MenuResponseDto {

    private final Long menuId;
    private final String imageUrl;
    private final String name;
    private final String description;
    private final int price;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public MenuResponseDto(Long menuId,
                           String imageUrl,
                           String name,
                           String description,
                           int price,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt
    ) {
        this.menuId = menuId;
        this.imageUrl = imageUrl;
        this.name = name;
        this.description = description;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
