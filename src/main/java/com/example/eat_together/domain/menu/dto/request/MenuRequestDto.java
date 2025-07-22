package com.example.eat_together.domain.menu.dto.request;

import lombok.Getter;

@Getter
public class MenuRequestDto {

    private final String imageUrl;

    private final String name;

    private final double price;

    private final String description;

    public MenuRequestDto(String imageUrl, String name, double price, String description) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
