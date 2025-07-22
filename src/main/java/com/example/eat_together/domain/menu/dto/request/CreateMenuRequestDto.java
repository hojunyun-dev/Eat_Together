package com.example.eat_together.domain.menu.dto.request;

import lombok.Getter;

@Getter
public class CreateMenuRequestDto {

    private final String imageUrl;

    private final String name;

    private final double price;

    private final String description;

    public CreateMenuRequestDto(String imageUrl, String name, double price, String description) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
