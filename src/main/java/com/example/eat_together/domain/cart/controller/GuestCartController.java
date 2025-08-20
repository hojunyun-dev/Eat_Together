package com.example.eat_together.domain.cart.controller;

import com.example.eat_together.domain.cart.dto.request.CartItemRequestDto;
import com.example.eat_together.domain.cart.dto.response.CartResponseDto;
import com.example.eat_together.domain.cart.service.GuestCartService;
import com.example.eat_together.global.dto.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * {@code GuestCartController}
 * 비회원(게스트) 장바구니 기능을 처리하는 컨트롤러
 * <p>
 * 게스트 사용자는 UUID 기반 식별자를 쿠키로 발급받아 장바구니 유지
 * 로그인 또는 회원가입 시 게스트 장바구니를 회원 장바구니와 병합 가능
 * </p>
 */
@RestController
@RequiredArgsConstructor
public class GuestCartController {

    /**
     * 게스트 장바구니 식별자 쿠키명
     */
    private static final String COOKIE_NAME = "GUEST_CART_ID";

    private final GuestCartService guestCartService;

    /**
     * 게스트 ID를 쿠키에서 조회하거나, 없으면 발급 후 쿠키에 저장
     *
     * @param req HTTP 요청 객체
     * @param res HTTP 응답 객체
     * @return 게스트 장바구니 식별자(UUID)
     */
    private String getOrIssueGuestId(HttpServletRequest req, HttpServletResponse res) {
        if (req.getCookies() != null) {
            Optional<String> existing = Arrays.stream(req.getCookies())
                    .filter(c -> COOKIE_NAME.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst();
            if (existing.isPresent()) return existing.get();
        }
        String uuid = UUID.randomUUID().toString();
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, uuid)
                .httpOnly(true)
                .path("/")
                .maxAge(60L * 60 * 24 * 14) // 14일
                .sameSite("Lax")
                .build();
        res.addHeader("Set-Cookie", cookie.toString());
        return uuid;
    }

    /**
     * 게스트 장바구니에 항목 추가
     *
     * @param req     HTTP 요청 객체
     * @param res     HTTP 응답 객체
     * @param storeId 매장 ID
     * @param dto     메뉴 및 수량 정보
     * @return 추가 완료 응답
     */
    @PostMapping("/stores/{storeId}/guest-carts/items")
    public ResponseEntity<ApiResponse<Void>> addItem(
            HttpServletRequest req, HttpServletResponse res,
            @PathVariable Long storeId,
            @RequestBody CartItemRequestDto dto
    ) {
        String gid = getOrIssueGuestId(req, res);
        guestCartService.addItem(gid, storeId, dto);
        return ResponseEntity.ok(ApiResponse.of(null, "게스트 장바구니에 담았습니다."));
    }

    /**
     * 게스트 장바구니 조회 (쿠키 기준)
     *
     * @param req HTTP 요청 객체
     * @param res HTTP 응답 객체
     * @return 장바구니 데이터와 조회 완료 응답
     */
    @GetMapping("/guest-carts/me")
    public ResponseEntity<ApiResponse<CartResponseDto>> getCart(
            HttpServletRequest req, HttpServletResponse res
    ) {
        String gid = getOrIssueGuestId(req, res);
        CartResponseDto dto = guestCartService.getCart(gid);
        return ResponseEntity.ok(ApiResponse.of(dto, "게스트 장바구니 조회"));
    }

    /**
     * 게스트 장바구니의 특정 메뉴 수량 변경
     *
     * @param req      HTTP 요청 객체
     * @param res      HTTP 응답 객체
     * @param menuId   메뉴 ID
     * @param quantity 변경할 수량
     * @return 수량 변경 완료 응답
     */
    @PatchMapping("/guest-carts/items/{menuId}")
    public ResponseEntity<ApiResponse<Void>> update(
            HttpServletRequest req, HttpServletResponse res,
            @PathVariable Long menuId,
            @RequestParam int quantity
    ) {
        String gid = getOrIssueGuestId(req, res);
        guestCartService.updateItem(gid, menuId, quantity);
        return ResponseEntity.ok(ApiResponse.of(null, "수량 변경"));
    }

    /**
     * 게스트 장바구니에서 특정 메뉴 삭제
     *
     * @param req    HTTP 요청 객체
     * @param res    HTTP 응답 객체
     * @param menuId 삭제할 메뉴 ID
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/guest-carts/items/{menuId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            HttpServletRequest req, HttpServletResponse res,
            @PathVariable Long menuId
    ) {
        String gid = getOrIssueGuestId(req, res);
        guestCartService.deleteItem(gid, menuId);
        return ResponseEntity.ok(ApiResponse.of(null, "삭제 완료"));
    }

    /**
     * 로그인 또는 회원가입 직후 게스트 장바구니를 회원 장바구니와 병합
     *
     * @param userDetails 로그인 사용자 정보
     * @param req         HTTP 요청 객체
     * @return 병합 완료 응답
     */
    @PostMapping("/guest-carts/merge")
    public ResponseEntity<ApiResponse<Void>> merge(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest req
    ) {
        String gid = Arrays.stream(Optional.ofNullable(req.getCookies()).orElse(new Cookie[0]))
                .filter(c -> COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
        if (gid != null) {
            Long userId = Long.valueOf(userDetails.getUsername());
            guestCartService.mergeToUserCart(gid, userId);
        }
        return ResponseEntity.ok(ApiResponse.of(null, "게스트 장바구니 머지 완료"));
    }
}
