package com.example.eat_together.domain.cart.controller;

import com.example.eat_together.domain.cart.dto.CartItemRequestDto;
import com.example.eat_together.domain.cart.dto.CartResponseDto;
import com.example.eat_together.domain.cart.service.CartService;
import com.example.eat_together.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CartController {

    private final CartService cartService;

    // 1. 메뉴 장바구니에 추가 (storeId를 경로로 받음)
    @PostMapping("/stores/{storeId}/carts/items")
    public ResponseEntity<ApiResponse<Void>> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long storeId,
            @Valid @RequestBody CartItemRequestDto requestDto) {

        cartService.addItem(Long.valueOf(userDetails.getUsername()), storeId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(null, "장바구니에 메뉴가 추가되었습니다."));
    }

    // 2. 장바구니 조회
    @GetMapping("/carts/me")
    public ResponseEntity<ApiResponse<CartResponseDto>> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {

        CartResponseDto responseDto = cartService.getCart(Long.valueOf(userDetails.getUsername()));
        return ResponseEntity.ok(ApiResponse.of(responseDto, "장바구니를 조회했습니다."));
    }

    // 3. 장바구니 항목 수량 수정
    @PatchMapping("/carts/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> updateQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemRequestDto requestDto) {

        cartService.updateQuantity(itemId, requestDto);
        return ResponseEntity.ok(ApiResponse.of(null, "수량이 수정되었습니다."));
    }

    // 4. 장바구니 항목 삭제
    @DeleteMapping("/carts/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long itemId) {
        cartService.deleteItem(itemId);
        return ResponseEntity.ok(ApiResponse.of(null, "장바구니에서 항목이 삭제되었습니다."));
    }
}
