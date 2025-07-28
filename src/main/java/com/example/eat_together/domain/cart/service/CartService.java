package com.example.eat_together.domain.cart.service;

import com.example.eat_together.domain.cart.dto.request.CartItemRequestDto;
import com.example.eat_together.domain.cart.dto.response.CartItemResponseDto;
import com.example.eat_together.domain.cart.dto.response.CartResponseDto;
import com.example.eat_together.domain.cart.entity.Cart;
import com.example.eat_together.domain.cart.entity.CartItem;
import com.example.eat_together.domain.cart.repository.CartItemRepository;
import com.example.eat_together.domain.cart.repository.CartRepository;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.menu.repository.MenuRepository;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.repository.StoreRepository;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    // 1. 장바구니에 메뉴 추가
    @Transactional
    public void addItem(Long userId, Long storeId, CartItemRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Menu menu = menuRepository.findById(requestDto.getMenuId())
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.of(user)));

        // 기존 장바구니와 다른 매장의 메뉴 담기 차단
        if (!cart.getCartItems().isEmpty()) {
            Long existingStoreId = cart.getCartItems().get(0).getMenu().getStore().getStoreId();
            if (!existingStoreId.equals(storeId)) {
                throw new CustomException(ErrorCode.CART_INVALID_STORE);
            }
        }

        // 수량 누적 검사(ex. 누적 수량이 90일 때 +10 이상이 들어올 경우)
        cart.getCartItems().stream()
                .filter(item -> item.getMenu().getMenuId().equals(menu.getMenuId()))
                .findFirst()
                .ifPresentOrElse(
                        item -> {
                            int newTotal = item.getQuantity() + requestDto.getQuantity();
                            if (newTotal > 99) {
                                throw new CustomException(ErrorCode.CART_EXCEEDS_MAX_QUANTITY);
                            }
                            item.updateQuantity(newTotal);
                        },
                        () -> {
                            if (requestDto.getQuantity() > 99) {
                                throw new CustomException(ErrorCode.CART_EXCEEDS_MAX_QUANTITY);
                            }
                            CartItem newItem = CartItem.of(menu, requestDto.getQuantity());
                            cart.addCartItem(newItem);
                        }
                );
    }

    // 2. 장바구니 전체 조회
    @Transactional
    public CartResponseDto getCart(Long userId) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        List<CartItemResponseDto> itemDtos = cart.getCartItems().stream()
                .map(CartItemResponseDto::new)
                .collect(Collectors.toList());

        if (itemDtos.isEmpty()) {
            throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        Store store = cart.getCartItems().get(0).getMenu().getStore();
        Long storeId = store.getStoreId();
        double deliveryFee = store.getDeliveryFee();

        return new CartResponseDto(storeId, itemDtos, deliveryFee);
    }

    // 3. 장바구니 수량 수정
    @Transactional
    public void updateQuantity(Long itemId, CartItemRequestDto requestDto) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItem.updateQuantity(requestDto.getQuantity());
    }

    // 4. 장바구니 항목 삭제
    @Transactional
    public void deleteItem(Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItemRepository.delete(cartItem);
    }
}
