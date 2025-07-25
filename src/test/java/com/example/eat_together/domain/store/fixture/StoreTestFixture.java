package com.example.eat_together.domain.store.fixture;

import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.entity.category.FoodCategory;
import com.example.eat_together.domain.user.entity.User;

import java.time.LocalTime;

public class StoreTestFixture {

    public static Store 매장_생성(User user) {
        return Store.of(
                user,
                "테스트매장",
                "테스트용 소개입니다.",
                "어딘가의 테스트 주소",
                true,
                LocalTime.of(9, 0),
                LocalTime.of(21, 0),
                2000.0,
                FoodCategory.KOREAN,
                "010-1234-5678"
        );
    }
}
