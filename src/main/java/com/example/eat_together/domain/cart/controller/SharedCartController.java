package com.example.eat_together.domain.cart.controller;

import com.example.eat_together.domain.cart.dto.request.SharedCartItemRequestDto;
import com.example.eat_together.domain.cart.dto.response.SharedCartResponseDto;
import com.example.eat_together.domain.cart.service.SharedCartService;
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
@RequestMapping("/chats/{roomId}/stores/{storeId}/shared-carts")
public class SharedCartController {

    private final SharedCartService sharedCartService;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<Void>> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long roomId,
            @PathVariable Long storeId,
            @Valid @RequestBody SharedCartItemRequestDto requestDto
    ) {
        Long userId = Long.valueOf(userDetails.getUsername());
        sharedCartService.addItem(userId, roomId, storeId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(null, "공유 장바구니에 메뉴가 추가되었습니다."));
    }

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<SharedCartResponseDto>> getAllItems(
            @PathVariable Long roomId,
            @PathVariable Long storeId
    ) {
        SharedCartResponseDto response = sharedCartService.getSharedCartItems(roomId);
        return ResponseEntity.ok(ApiResponse.of(response, "공유 장바구니 항목을 조회했습니다."));
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> updateItemQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long roomId,
            @PathVariable Long storeId,
            @PathVariable Long itemId,
            @Valid @RequestBody SharedCartItemRequestDto requestDto
    ) {
        Long userId = Long.valueOf(userDetails.getUsername());
        sharedCartService.updateQuantity(userId, roomId, itemId, requestDto);
        return ResponseEntity.ok(ApiResponse.of(null, "공유 장바구니 항목 수량이 수정되었습니다."));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long roomId,
            @PathVariable Long storeId,
            @PathVariable Long itemId
    ) {
        Long userId = Long.valueOf(userDetails.getUsername());
        sharedCartService.deleteItem(userId, roomId, itemId);
        return ResponseEntity.ok(ApiResponse.of(null, "공유 장바구니 항목이 삭제되었습니다."));
    }

}
