package com.example.eat_together.domain.cart.service;

import com.example.eat_together.domain.cart.dto.CartItemRequestDto;
import com.example.eat_together.domain.cart.dto.CartItemResponseDto;
import com.example.eat_together.domain.cart.dto.CartResponseDto;
import com.example.eat_together.domain.cart.entity.Cart;
import com.example.eat_together.domain.cart.entity.CartItem;
import com.example.eat_together.domain.cart.repository.CartItemRepository;
import com.example.eat_together.domain.cart.repository.CartRepository;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.menu.repository.MenuRepository;
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

    // 1. 장바구니에 메뉴 추가
    @Transactional
    public void addItem(Long userId, CartItemRequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Menu menu = menuRepository.findById(requestDto.getMenuId())
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.of(user)));

        // 기존 장바구니에 동일 메뉴가 있다면 수량 증가
        cart.getCartItems().stream()
                .filter(item -> item.getMenu().getMenuId().equals(menu.getMenuId())) // TODO : 이거 고침
                .findFirst()
                .ifPresentOrElse(
                        item -> item.updateQuantity(item.getQuantity() + requestDto.getQuantity()),
                        () -> {
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

        return new CartResponseDto(itemDtos);
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
