package com.example.eat_together.domain.menu.fixture;

import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.store.entity.Store;
import org.springframework.test.util.ReflectionTestUtils;

public class MenuTestFixture {

    public static Menu 메뉴_생성(Store store) {
        Menu menu = Menu.of(
                store,
                "테스트용 이미지",
                "테스트용 이름",
                3500.0,
                "테스트용 소개"
        );
        ReflectionTestUtils.setField(menu, "menuId", 1L);

        return menu;
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
