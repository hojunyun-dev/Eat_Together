package com.example.eat_together.store.category;

import lombok.Getter;

@Getter
public enum FoodCategory {

    KOREAN("한식"),
    CHINESE("중식"),
    JAPANESE("일식"),
    WESTERN("양식"),
    FASTFOOD("패스트푸드"),
    OTHER("기타");


    private final String category;

    FoodCategory(String category) {
        this.category = category;
    }
}
