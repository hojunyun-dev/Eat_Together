package com.example.eat_together.domain.menu.controller;

import com.example.eat_together.domain.menu.dto.request.CreateMenuRequestDto;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.menu.service.MenuService;
import com.example.eat_together.global.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stores/{storeId}/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createMenu(@PathVariable Long storeId, @RequestBody CreateMenuRequestDto requestDto) {

        menuService.createMenu(storeId, requestDto);

        ApiResponse<Menu> response = new ApiResponse<>("메뉴 등록이 완료되었습니다.", null);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
