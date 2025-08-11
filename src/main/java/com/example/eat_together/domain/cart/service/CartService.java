package com.example.eat_together.domain.cart.service;

import com.example.eat_together.domain.cart.dto.request.CartItemRequestDto;
import com.example.eat_together.domain.cart.dto.response.CartItemResponseDto;
import com.example.eat_together.domain.cart.dto.response.CartResponseDto;
import com.example.eat_together.domain.cart.entity.Cart;
import com.example.eat_together.domain.cart.entity.CartItem;
import com.example.eat_together.domain.cart.entity.SharedCartItem;
import com.example.eat_together.domain.cart.repository.CartItemRepository;
import com.example.eat_together.domain.cart.repository.CartRepository;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.menu.repository.MenuRepository;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.repository.StoreRepository;
import com.example.eat_together.domain.users.common.entity.User;
import com.example.eat_together.domain.users.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 장바구니 도메인의 비즈니스 로직을 담당하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    /**
     * 장바구니에 메뉴 항목 추가
     *
     * @param userId     사용자 ID
     * @param storeId    매장 ID
     * @param requestDto 메뉴 및 수량 정보
     */
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

        if (!cart.getCartItems().isEmpty()) {
            Long existingStoreId = cart.getCartItems().get(0).getMenu().getStore().getStoreId();
            if (!existingStoreId.equals(storeId)) {
                throw new CustomException(ErrorCode.CART_INVALID_STORE);
            }
        }

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

    /**
     * 사용자 장바구니 전체 조회
     *
     * @param userId 사용자 ID
     * @return 장바구니 응답 DTO
     */
    @Transactional
    public CartResponseDto getCart(Long userId) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        List<CartItemResponseDto> itemDtos = cart.getCartItems().stream()
                .map(CartItemResponseDto::from)
                .collect(Collectors.toList());

        if (itemDtos.isEmpty()) {
            throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        Long storeId = cart.getCartItems().get(0).getMenu().getStore().getStoreId();
        double deliveryFee = cart.getDeliveryFee();
        double subPrice = itemDtos.stream().mapToDouble(CartItemResponseDto::getTotalPrice).sum();

        return CartResponseDto.builder()
                .storeId(storeId)
                .content(itemDtos)
                .subPrice(subPrice)
                .deliveryFee(deliveryFee)
                .storeTotalPrice(subPrice + deliveryFee)
                .build();
    }



    /**
     * 장바구니 항목 수량 수정
     *
     * @param itemId     항목 ID
     * @param requestDto 수정할 수량 정보
     */
    @Transactional
    public void updateQuantity(Long itemId, CartItemRequestDto requestDto) {
        if (requestDto.getQuantity() > 99) {
            throw new CustomException(ErrorCode.CART_EXCEEDS_MAX_QUANTITY);
        }

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItem.updateQuantity(requestDto.getQuantity());
    }


    /**
     * 장바구니 항목 삭제
     *
     * @param itemId 삭제할 항목 ID
     */
    @Transactional
    public void deleteItem(Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItemRepository.delete(cartItem);
    }
}
