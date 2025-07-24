package com.example.eat_together.domain.store.entity.category;

import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.Getter;

import java.util.Arrays;

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

    // 쿼리 파라미터로 받은 값 enum으로 변환
    // ex) 한식 = KOREAN
    public static FoodCategory fromKr(String category) {
        return Arrays.stream(FoodCategory.values())
                .filter(c -> c.category.equals(category))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }
}
