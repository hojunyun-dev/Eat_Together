package com.example.eat_together.domain.menu.fixture;

import com.example.eat_together.domain.menu.dto.request.MenuRequestDto;
import com.example.eat_together.domain.menu.dto.request.MenuUpdateRequestDto;

public class MenuTestDtoFixture {

    public static String MENU_IMAGE_URL = "테스트용 이미지";
    public static String MENU_NAME = "테스트용 이름";
    public static double MENU_PRICE = 3500.0;
    public static String MENU_DESCRIPTION = "테스트용 메뉴 소개";

    public static MenuRequestDto requestDtoMock() {
        return new MenuRequestDto(MENU_IMAGE_URL, MENU_NAME, MENU_PRICE, MENU_DESCRIPTION);
    }

    public static MenuUpdateRequestDto updateRequestDtoMock() {
        return new MenuUpdateRequestDto("수정된 " + MENU_IMAGE_URL, "수정된 " + MENU_NAME, 1000.0 + MENU_PRICE, "수정된 " + MENU_DESCRIPTION);
    }

}
