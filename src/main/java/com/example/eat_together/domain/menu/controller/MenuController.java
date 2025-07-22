package com.example.eat_together.domain.menu.controller;

import com.example.eat_together.domain.menu.dto.request.CreateMenuRequestDto;
import com.example.eat_together.domain.menu.dto.respones.PagingMenuResponseDto;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.menu.message.ResponseMessage;
import com.example.eat_together.domain.menu.service.MenuService;
import com.example.eat_together.global.dto.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

        ApiResponse<Menu> response = new ApiResponse<>(ResponseMessage.MENU_CREATED_SUCCESS.getMessage(), null);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagingMenuResponseDto>> getMenusByStore(@PathVariable Long storeId, @PageableDefault Pageable pageable) {

        PagingMenuResponseDto menusByStoreId = menuService.getMenusByStoreId(storeId, pageable);

        ApiResponse<PagingMenuResponseDto> response = new ApiResponse<>(ResponseMessage.MENU_LIST_FETCH_SUCCESS.getMessage(), menusByStoreId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
