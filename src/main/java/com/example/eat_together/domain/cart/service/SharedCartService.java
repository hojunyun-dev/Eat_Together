package com.example.eat_together.domain.cart.service;

import com.example.eat_together.domain.cart.dto.request.SharedCartItemRequestDto;
import com.example.eat_together.domain.cart.dto.response.SharedCartItemResponseDto;
import com.example.eat_together.domain.cart.dto.response.SharedCartResponseDto;
import com.example.eat_together.domain.cart.entity.SharedCart;
import com.example.eat_together.domain.cart.entity.SharedCartItem;
import com.example.eat_together.domain.cart.repository.SharedCartItemRepository;
import com.example.eat_together.domain.cart.repository.SharedCartRepository;
import com.example.eat_together.domain.chat.entity.ChatRoom;
import com.example.eat_together.domain.chat.repository.ChatRoomRepository;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.menu.repository.MenuRepository;
import com.example.eat_together.domain.users.common.entity.User;
import com.example.eat_together.domain.users.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SharedCartService {

    private final SharedCartRepository sharedCartRepository;
    private final SharedCartItemRepository sharedCartItemRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    /**
     * 공유 장바구니 담기/수량증가
     */
    @Transactional
    public void addItem(Long userId, Long roomId, Long storeId, SharedCartItemRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Menu menu = menuRepository.findById(requestDto.getMenuId())
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM));

        SharedCart sharedCart = sharedCartRepository.findByChatRoomId(roomId)
                .orElseGet(() -> sharedCartRepository.save(SharedCart.of(chatRoom)));

        // 같은 방 내 다른 매장 메뉴를 섞어서 담지 못하게 제약
        if (!sharedCart.getItems().isEmpty()) {
            Long existingStoreId = sharedCart.getItems().get(0).getMenu().getStore().getStoreId();
            if (!existingStoreId.equals(storeId)) {
                throw new CustomException(ErrorCode.CART_INVALID_STORE);
            }
        }

        sharedCart.getItems().stream()
                .filter(item -> item.getMenu().getMenuId().equals(menu.getMenuId())
                        && item.getUser().getUserId().equals(userId))
                .findFirst()
                .ifPresentOrElse(
                        item -> {
                            int newQuantity = item.getQuantity() + requestDto.getQuantity();
                            if (newQuantity > 99) throw new CustomException(ErrorCode.CART_EXCEEDS_MAX_QUANTITY);
                            item.updateQuantity(newQuantity);
                        },
                        () -> {
                            if (requestDto.getQuantity() > 99) throw new CustomException(ErrorCode.CART_EXCEEDS_MAX_QUANTITY);
                            SharedCartItem newItem = SharedCartItem.of(menu, user, requestDto.getQuantity());
                            sharedCart.addItem(newItem);
                        }
                );
    }

    /**
     * 공유 장바구니 조회
     * - 현재 "아이템을 담은 유저 수" 기준으로 배달팁 1/n 계산 → 응답 DTO에만 반영(표시용)
     */
    @Transactional(readOnly = true)
    public SharedCartResponseDto getSharedCartItems(Long roomId) {
        List<SharedCartItem> items = sharedCartItemRepository.findBySharedCart_ChatRoom_Id(roomId);
        if (items.isEmpty()) throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);

        Long storeId = items.get(0).getMenu().getStore().getStoreId();
        double deliveryFee = items.get(0).getMenu().getStore().getDeliveryFee();

        long userCountWithItems = items.stream()
                .map(item -> item.getUser().getUserId())
                .distinct()
                .count();

        double deliveryFeePerUser = deliveryFee / userCountWithItems;

        List<SharedCartItemResponseDto> itemDtos = items.stream()
                .map(item -> SharedCartItemResponseDto.from(item, deliveryFeePerUser))
                .toList();

        return SharedCartResponseDto.builder()
                .storeId(storeId)
                .deliveryFee(deliveryFee)
                .items(itemDtos)
                .build();
    }

    /**
     * 공유 장바구니 수량 변경
     */
    @Transactional
    public void updateQuantity(Long userId, Long roomId, Long itemId, SharedCartItemRequestDto requestDto) {
        SharedCartItem item = sharedCartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!item.getUser().getUserId().equals(userId)
                || !item.getSharedCart().getChatRoom().getId().equals(roomId)) {
            throw new CustomException(ErrorCode.SHARED_CART_ITEM_ACCESS_DENIED);
        }

        int newQuantity = requestDto.getQuantity();
        if (newQuantity < 1 || newQuantity > 99) {
            throw new CustomException(ErrorCode.CART_EXCEEDS_MAX_QUANTITY);
        }

        item.updateQuantity(newQuantity);
    }

    /**
     * 공유 장바구니 항목 삭제
     */
    @Transactional
    public void deleteItem(Long userId, Long roomId, Long itemId) {
        SharedCartItem item = sharedCartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!item.getUser().getUserId().equals(userId)
                || !item.getSharedCart().getChatRoom().getId().equals(roomId)) {
            throw new CustomException(ErrorCode.SHARED_CART_ITEM_ACCESS_DENIED);
        }

        sharedCartItemRepository.delete(item);
    }


}
