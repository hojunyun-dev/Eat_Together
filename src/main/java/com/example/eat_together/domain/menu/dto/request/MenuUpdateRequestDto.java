package com.example.eat_together.domain.menu.dto.request;

import lombok.Getter;

@Getter
public class MenuUpdateRequestDto {

    private final String imageUrl;

    private final String name;

    private final Double price;

    private final String description;

    public MenuUpdateRequestDto(String imageUrl, String name, double price, String description) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
