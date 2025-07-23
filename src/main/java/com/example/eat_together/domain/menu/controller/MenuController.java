package com.example.eat_together.domain.menu.controller;

import com.example.eat_together.domain.menu.dto.request.MenuRequestDto;
import com.example.eat_together.domain.menu.dto.respones.MenuResponseDto;
import com.example.eat_together.domain.menu.dto.respones.PagingMenuResponseDto;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.menu.message.ResponseMessage;
import com.example.eat_together.domain.menu.service.MenuService;
import com.example.eat_together.global.dto.ApiResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse> createMenu(@PathVariable Long storeId,
                                                  @Valid @RequestBody MenuRequestDto requestDto) {

        menuService.createMenu(storeId, requestDto);

        ApiResponse<Menu> response = new ApiResponse<>
                (
                        ResponseMessage.MENU_CREATED_SUCCESS.getMessage(),
                        null
                );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagingMenuResponseDto>> getMenusByStore(@PathVariable Long storeId,
                                                                              @PageableDefault Pageable pageable) {

        PagingMenuResponseDto menusByStoreId = menuService.getMenusByStoreId(storeId, pageable);

        ApiResponse<PagingMenuResponseDto> response = new ApiResponse<>
                (
                        ResponseMessage.MENU_LIST_FETCH_SUCCESS.getMessage(),
                        menusByStoreId
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<ApiResponse<MenuResponseDto>> getMenuByStore(@PathVariable Long storeId,
                                                                       @PathVariable Long menuId) {

        MenuResponseDto menuByStore = menuService.getMenuByStore(storeId, menuId);

        ApiResponse<MenuResponseDto> response = new ApiResponse<>
                (
                        ResponseMessage.MENU_FETCH_SUCCESS.getMessage(),
                        menuByStore
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{menuId}")
    public ResponseEntity<ApiResponse<MenuResponseDto>> updateMenu(@PathVariable Long storeId,
                                                                   @PathVariable Long menuId,
                                                                   @RequestBody MenuRequestDto request) {

        MenuResponseDto responseDto = menuService.updateMenu(storeId, menuId, request);

        ApiResponse<MenuResponseDto> response = new ApiResponse<>
                (
                        ResponseMessage.MENU_UPDATED_SUCCESS.getMessage(),
                        responseDto
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<ApiResponse> deletedMenu(@PathVariable Long storeId,
                                                   @PathVariable Long menuId) {

        menuService.deleteMenu(storeId, menuId);

        ApiResponse<Menu> response = new ApiResponse<>(ResponseMessage.MENU_DELETED_SUCCESS.getMessage(), null);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
