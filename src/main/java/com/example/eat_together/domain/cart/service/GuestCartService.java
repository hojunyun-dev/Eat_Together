package com.example.eat_together.domain.cart.service;

import com.example.eat_together.domain.cart.dto.request.CartItemRequestDto;
import com.example.eat_together.domain.cart.dto.response.CartResponseDto;
import com.example.eat_together.domain.cart.entity.Cart;
import com.example.eat_together.domain.cart.entity.CartItem;
import com.example.eat_together.domain.cart.repository.CartRepository;
import com.example.eat_together.domain.cart.repository.GuestCartRepository;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.menu.repository.MenuRepository;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.repository.StoreRepository;
import com.example.eat_together.domain.users.common.entity.User;
import com.example.eat_together.domain.users.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 비회원(게스트) 장바구니 도메인의 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class GuestCartService {

    private final GuestCartRepository guestCartRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    /**
     * 게스트 ID 문자열을 해시값으로 변환
     *
     * @param guestCartId 게스트 장바구니 ID(UUID)
     * @return 해시값
     */
    private Long hash(String guestCartId) {
        return Math.abs(guestCartId.hashCode() + 1469598103934665603L);
    }

    /**
     * 게스트 장바구니에 항목 추가
     *
     * @param guestCartId 게스트 장바구니 ID
     * @param storeId     매장 ID
     * @param req         메뉴 및 수량 정보
     */
    @Transactional
    public void addItem(String guestCartId, Long storeId, CartItemRequestDto req) {
        Long key = hash(guestCartId);

        Menu menu = menuRepository.findById(req.getMenuId())
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

        storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (!menu.getStore().getStoreId().equals(storeId)) {
            throw new CustomException(ErrorCode.CART_INVALID_STORE);
        }

        Long existingStore = guestCartRepository.getStore(key);
        if (existingStore == null) {
            guestCartRepository.setStore(key, storeId);
        } else if (!existingStore.equals(storeId)) {
            throw new CustomException(ErrorCode.CART_INVALID_STORE);
        }

        Map<String, String> items = guestCartRepository.getAllItems(key);
        String k = String.valueOf(req.getMenuId());
        int cur = Integer.parseInt(items.getOrDefault(k, "0"));

        int next = cur + req.getQuantity();
        if (next < 1 || next > 99) {
            throw new CustomException(ErrorCode.CART_EXCEEDS_MAX_QUANTITY);
        }

        guestCartRepository.putItem(key, req.getMenuId(), next);
    }

    /**
     * 게스트 장바구니 조회
     *
     * @param guestCartId 게스트 장바구니 ID
     * @return 장바구니 응답 DTO
     */
    @Transactional(readOnly = true)
    public CartResponseDto getCart(String guestCartId) {
        Long key = hash(guestCartId);
        Long storeId = guestCartRepository.getStore(key);
        Map<String, String> items = guestCartRepository.getAllItems(key);

        if (storeId == null || items.isEmpty()) {
            throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
        double deliveryFee = store.getDeliveryFee();

        var content = items.entrySet().stream()
                .map(e -> {
                    Long menuId = Long.valueOf(e.getKey());
                    int qty = Integer.parseInt(e.getValue());
                    Menu menu = menuRepository.findById(menuId)
                            .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));
                    double price = menu.getPrice();
                    return com.example.eat_together.domain.cart.dto.response.CartItemResponseDto.builder()
                            .itemId(menuId) // 게스트는 itemId 대신 menuId 사용
                            .menuName(menu.getName())
                            .quantity(qty)
                            .price(price)
                            .totalPrice(price * qty)
                            .build();
                })
                .toList();

        double sub = content.stream()
                .mapToDouble(com.example.eat_together.domain.cart.dto.response.CartItemResponseDto::getTotalPrice)
                .sum();

        return com.example.eat_together.domain.cart.dto.response.CartResponseDto.builder()
                .storeId(storeId)
                .content(content)
                .subPrice(sub)
                .deliveryFee(deliveryFee)
                .storeTotalPrice(sub + deliveryFee)
                .build();
    }

    /**
     * 게스트 장바구니 항목 수량 변경
     *
     * @param guestCartId 게스트 장바구니 ID
     * @param menuId      메뉴 ID
     * @param quantity    변경할 수량
     */
    @Transactional
    public void updateItem(String guestCartId, Long menuId, int quantity) {
        Long key = hash(guestCartId);
        if (quantity < 1 || quantity > 99) {
            throw new CustomException(ErrorCode.CART_EXCEEDS_MAX_QUANTITY);
        }
        guestCartRepository.putItem(key, menuId, quantity);
    }

    /**
     * 게스트 장바구니 항목 삭제
     *
     * @param guestCartId 게스트 장바구니 ID
     * @param menuId      메뉴 ID
     */
    @Transactional
    public void deleteItem(String guestCartId, Long menuId) {
        Long key = hash(guestCartId);
        guestCartRepository.removeItem(key, menuId);
    }

    /**
     * 게스트 장바구니를 회원 장바구니로 병합
     *
     * @param guestCartId 게스트 장바구니 ID
     * @param userId      사용자 ID
     */
    @Transactional
    public void mergeToUserCart(String guestCartId, Long userId) {
        Long key = hash(guestCartId);
        Map<String, String> items = guestCartRepository.getAllItems(key);
        if (items == null || items.isEmpty()) return;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.of(user)));

        Long guestStoreId = guestCartRepository.getStore(key);

        if (!cart.getCartItems().isEmpty()) {
            Long existingStoreId = cart.getCartItems().get(0).getMenu().getStore().getStoreId();
            if (guestStoreId != null && !existingStoreId.equals(guestStoreId)) {
                throw new CustomException(ErrorCode.CART_INVALID_STORE);
            }
        }

        if (guestStoreId != null) {
            Store store = storeRepository.findById(guestStoreId)
                    .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
            if (cart.getCartItems().isEmpty() || cart.getDeliveryFee() == 0.0) {
                cart.setDeliveryFee(store.getDeliveryFee());
            }
        }

        for (var e : items.entrySet()) {
            Long menuId = Long.valueOf(e.getKey());
            int addQty = Integer.parseInt(e.getValue());

            Menu menu = menuRepository.findById(menuId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

            var existing = cart.getCartItems().stream()
                    .filter(ci -> ci.getMenu().getMenuId().equals(menuId))
                    .findFirst();

            if (existing.isPresent()) {
                int next = existing.get().getQuantity() + addQty;
                if (next > 99) next = 99;
                existing.get().updateQuantity(next);
            } else {
                CartItem newItem = CartItem.of(menu, Math.min(addQty, 99));
                cart.addCartItem(newItem);
            }
        }

        guestCartRepository.deleteCart(key);
    }
}