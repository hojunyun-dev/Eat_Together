package com.example.eat_together.domain.menu.fixture;

import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.store.entity.Store;

public class MenuTestFixture {

    public static Menu 메뉴_생성(Store store) {
        return Menu.of(
                store,
                "테스트용 이미지",
                "테스트용 이름",
                3500.0,
                "테스트용 소개"
        );
    }

    public static Menu 리스트용_메뉴_생성(Store store, String name) {
        return Menu.of(
                store,
                "테스트용 이미지",
                name,
                3500.0,
                "테스트용 소개"
        );
    }
}
