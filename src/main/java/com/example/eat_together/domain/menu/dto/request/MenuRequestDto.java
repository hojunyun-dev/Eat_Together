package com.example.eat_together.domain.menu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MenuRequestDto {

    @NotBlank(message = "이미지가 없습니다.")
    private final String imageUrl;

    @NotBlank(message = "메뉴 이름이 없습니다.")
    private final String name;

    @NotNull(message = "가격이 없습니다.")
    private final double price;

    @NotBlank(message = "메뉴 설명이 없습니다.")
    private final String description;

    public MenuRequestDto(String imageUrl, String name, double price, String description) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
