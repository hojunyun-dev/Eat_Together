package com.example.eat_together.domain.cart.controller;

import com.example.eat_together.domain.cart.dto.request.CartItemRequestDto;
import com.example.eat_together.domain.cart.dto.response.CartResponseDto;
import com.example.eat_together.domain.cart.service.CartService;
import com.example.eat_together.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * {@code CartController}
 * 로그인 사용자의 장바구니 기능을 처리하는 컨트롤러
 * <p>
 * 장바구니 항목 추가, 조회, 수량 수정, 삭제 기능 제공
 * </p>
 */
@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니에 메뉴 항목 추가
     *
     * @param userDetails 로그인 사용자 정보
     * @param storeId     매장 ID
     * @param requestDto  메뉴 및 수량 정보
     * @return HTTP 201(Created) 상태 코드와 추가 완료 응답
     */
    @PostMapping("/stores/{storeId}/carts/items")
    public ResponseEntity<ApiResponse<Void>> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long storeId,
            @Valid @RequestBody CartItemRequestDto requestDto) {

        cartService.addItem(Long.valueOf(userDetails.getUsername()), storeId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(null, "장바구니에 메뉴가 추가되었습니다."));
    }

    /**
     * 로그인 사용자의 장바구니 조회
     *
     * @param userDetails 로그인 사용자 정보
     * @return 장바구니 데이터와 조회 완료 응답
     */
    @GetMapping("/carts/me")
    public ResponseEntity<ApiResponse<CartResponseDto>> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {

        CartResponseDto responseDto = cartService.getCart(Long.valueOf(userDetails.getUsername()));
        return ResponseEntity.ok(ApiResponse.of(responseDto, "장바구니를 조회했습니다."));
    }

    /**
     * 장바구니 항목 수량 수정
     *
     * @param itemId     수정할 항목 ID
     * @param requestDto 변경할 수량 정보
     * @return 수정 완료 응답
     */
    @PatchMapping("/carts/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> updateQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemRequestDto requestDto) {

        cartService.updateQuantity(itemId, requestDto);
        return ResponseEntity.ok(ApiResponse.of(null, "수량이 수정되었습니다."));
    }

    /**
     * 장바구니 항목 삭제
     *
     * @param itemId 삭제할 항목 ID
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/carts/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long itemId) {
        cartService.deleteItem(itemId);
        return ResponseEntity.ok(ApiResponse.of(null, "장바구니에서 항목이 삭제되었습니다."));
    }
}
