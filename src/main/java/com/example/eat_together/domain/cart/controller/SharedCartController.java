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

/**
 * {@code SharedCartController}
 * 채팅방 기반의 공유 장바구니 기능을 처리하는 컨트롤러
 * <p>
 * 채팅방 참여자들이 동일한 매장에서 주문할 메뉴를 공유 장바구니에 담고
 * 조회, 수량 수정, 삭제할 수 있는 기능 제공
 * </p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/chats/{roomId}/stores/{storeId}/shared-carts")
public class SharedCartController {

    private final SharedCartService sharedCartService;

    /**
     * 공유 장바구니에 메뉴 항목 추가
     *
     * @param userDetails 로그인 사용자 정보
     * @param roomId      채팅방 ID
     * @param storeId     매장 ID
     * @param requestDto  메뉴 및 수량 정보
     * @return HTTP 201(Created) 상태 코드와 추가 완료 응답
     */
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

    /**
     * 공유 장바구니 항목 전체 조회
     *
     * @param userDetails 로그인 사용자 정보
     * @param roomId      채팅방 ID
     * @param storeId     매장 ID
     * @return 공유 장바구니 전체 항목과 조회 완료 응답
     */
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<SharedCartResponseDto>> getAllItems(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long roomId,
            @PathVariable Long storeId
    ) {
        Long userId = Long.valueOf(userDetails.getUsername());
        SharedCartResponseDto response = sharedCartService.getSharedCartItems(userId, roomId);
        return ResponseEntity.ok(ApiResponse.of(response, "공유 장바구니 항목을 조회했습니다."));
    }

    /**
     * 공유 장바구니의 특정 항목 수량 수정
     *
     * @param userDetails 로그인 사용자 정보
     * @param roomId      채팅방 ID
     * @param storeId     매장 ID
     * @param itemId      항목 ID
     * @param requestDto  변경할 수량 정보
     * @return 수량 수정 완료 응답
     */
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

    /**
     * 공유 장바구니에서 특정 항목 삭제
     *
     * @param userDetails 로그인 사용자 정보
     * @param roomId      채팅방 ID
     * @param storeId     매장 ID
     * @param itemId      항목 ID
     * @return 삭제 완료 응답
     */
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


