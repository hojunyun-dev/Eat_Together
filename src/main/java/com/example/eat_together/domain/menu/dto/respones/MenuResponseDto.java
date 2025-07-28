package com.example.eat_together.domain.menu.dto.respones;

import com.example.eat_together.domain.menu.entity.Menu;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MenuResponseDto {

    private Long menuId;
    private String imageUrl;
    private String name;
    private String description;
    private double price;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Builder
    public MenuResponseDto(Long menuId,
                           String imageUrl,
                           String name,
                           String description,
                           double price,
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

    public static MenuResponseDto from(Menu menu) {
        return new MenuResponseDto(
                menu.getMenuId(),
                menu.getImageUrl(),
                menu.getName(),
                menu.getDescription(),
                menu.getPrice(),
                menu.getCreatedAt(),
                menu.getUpdatedAt()
        );
    }
}
