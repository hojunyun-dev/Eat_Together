package com.example.eat_together.domain.menu.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuUpdateRequestDto {

    private String imageUrl;

    private String name;

    private Double price;

    private String description;

    public MenuUpdateRequestDto(String imageUrl, String name, double price, String description) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
