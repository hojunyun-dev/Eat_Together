package com.example.eat_together.domain.menu.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MenuRequestDto {

    @NotBlank(message = "이미지가 없습니다.")
    private final String imageUrl;

    @NotBlank(message = "메뉴 이름이 없습니다.")
    @Size(max = 50, message = "메뉴 이름은 50자 이하여야 합니다.")
    private final String name;

    @NotNull(message = "가격이 없습니다.")
    @Min(value = 0, message = "가격에 음수를 입력할 수 없습니다.")
    private final double price;

    @NotBlank(message = "메뉴 설명이 없습니다.")
    @Size(max = 255, message = "메뉴 설명은 255자 이하여야 합니다.")
    private final String description;

    public MenuRequestDto(String imageUrl, String name, double price, String description) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
