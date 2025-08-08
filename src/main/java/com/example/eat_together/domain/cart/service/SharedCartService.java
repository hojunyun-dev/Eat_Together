package com.example.eat_together.domain.cart.service;

import com.example.eat_together.domain.cart.dto.request.SharedCartItemRequestDto;
import com.example.eat_together.domain.cart.dto.response.SharedCartItemResponseDto;
import com.example.eat_together.domain.cart.dto.response.SharedCartResponseDto;
import com.example.eat_together.domain.cart.entity.Cart;
import com.example.eat_together.domain.cart.entity.CartItem;
import com.example.eat_together.domain.cart.entity.SharedCart;
import com.example.eat_together.domain.cart.entity.SharedCartItem;
import com.example.eat_together.domain.cart.repository.CartRepository;
import com.example.eat_together.domain.cart.repository.SharedCartItemRepository;
import com.example.eat_together.domain.cart.repository.SharedCartRepository;
import com.example.eat_together.domain.chat.entity.ChatRoom;
import com.example.eat_together.domain.chat.repository.ChatRoomRepository;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.menu.repository.MenuRepository;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.repository.UserRepository;
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
    private final CartRepository cartRepository;

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
                            if (newQuantity > 99) {
                                throw new CustomException(ErrorCode.CART_EXCEEDS_MAX_QUANTITY);
                            }
                            item.updateQuantity(newQuantity);
                        },
                        () -> {
                            if (requestDto.getQuantity() > 99) {
                                throw new CustomException(ErrorCode.CART_EXCEEDS_MAX_QUANTITY);
                            }
                            SharedCartItem newItem = SharedCartItem.of(menu, user, requestDto.getQuantity());
                            sharedCart.addItem(newItem);
                        }
                );

        updateAllDeliveryFees(sharedCart);
    }

    @Transactional(readOnly = true)
    public SharedCartResponseDto getSharedCartItems(Long roomId) {
        List<SharedCartItem> items = sharedCartItemRepository.findBySharedCart_ChatRoom_Id(roomId);

        if (items.isEmpty()) {
            throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

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


    @Transactional
    public void updateQuantity(Long userId, Long roomId, Long itemId, SharedCartItemRequestDto requestDto) {
        SharedCartItem item = sharedCartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!item.getUser().getUserId().equals(userId) ||
                !item.getSharedCart().getChatRoom().getId().equals(roomId)) {
            throw new CustomException(ErrorCode.SHARED_CART_ITEM_ACCESS_DENIED);
        }

        int newQuantity = requestDto.getQuantity();
        if (newQuantity < 1 || newQuantity > 99) {
            throw new CustomException(ErrorCode.CART_EXCEEDS_MAX_QUANTITY);
        }

        item.updateQuantity(newQuantity);
    }

    @Transactional
    public void deleteItem(Long userId, Long roomId, Long itemId) {
        SharedCartItem item = sharedCartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!item.getUser().getUserId().equals(userId) ||
                !item.getSharedCart().getChatRoom().getId().equals(roomId)) {
            throw new CustomException(ErrorCode.SHARED_CART_ITEM_ACCESS_DENIED);
        }

        SharedCart sharedCart = item.getSharedCart();
        sharedCartItemRepository.delete(item);

        updateAllDeliveryFees(sharedCart);
    }

    /**
     * 공유 장바구니 항목을 개인 장바구니로 복사하면서 배달팁 포함 총 금액을 반영하는 메서드
     */
    @Transactional
    public void moveToCart(Long userId, Long roomId) {
        // [1] 이 유저의 공유 장바구니 항목 조회
        List<SharedCartItem> myItems = sharedCartItemRepository
                .findBySharedCart_ChatRoom_IdAndUser_UserId(roomId, userId);

        if (myItems.isEmpty()) {
            throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        // [2] 전체 공유 장바구니 항목 조회 (삭제 전)
        List<SharedCartItem> allItemsBeforeDelete = sharedCartItemRepository
                .findBySharedCart_ChatRoom_Id(roomId);

        long userCountBeforeDelete = allItemsBeforeDelete.stream()
                .map(item -> item.getUser().getUserId())
                .distinct()
                .count();

        if (userCountBeforeDelete == 0) throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        double deliveryFee = allItemsBeforeDelete.get(0).getMenu().getStore().getDeliveryFee();
        double deliveryFeePerUser = deliveryFee / userCountBeforeDelete;

        // [3] 개인 장바구니 설정
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseGet(() -> Cart.of(userRepository.getReferenceById(userId)));

        cart.clearItems();
        cart.setDeliveryFee(deliveryFeePerUser); // ⛳ 배달팁은 삭제 이전 기준

        for (SharedCartItem sharedItem : myItems) {
            Menu menu = sharedItem.getMenu();
            CartItem newItem = CartItem.of(menu, sharedItem.getQuantity());
            cart.addCartItem(newItem);
        }

        cartRepository.save(cart);

    }




    /**
     * 공유 장바구니에 담긴 유저 수 기반으로 개인 배달팁 계산 후 SharedCartItem에 저장
     */
    @Transactional
    public void updateAllDeliveryFees(SharedCart sharedCart) {
        // ❗ 삭제 후 상태 반영을 위해 DB에서 새로 조회
        List<SharedCartItem> items = sharedCartItemRepository.findBySharedCart_ChatRoom_Id(
                sharedCart.getChatRoom().getId()
        );

        if (items.isEmpty()) return;

        double deliveryFee = items.get(0).getMenu().getStore().getDeliveryFee();

        // ✅ 삭제 이후 유저 수 반영을 위해 userId 기준 distinct
        long userCount = items.stream()
                .map(item -> item.getUser().getUserId())
                .distinct()
                .count();

        if (userCount == 0) return;

        double deliveryFeePerUser = deliveryFee / userCount;

        for (SharedCartItem item : items) {
            item.setDeliveryFeePerUser(deliveryFeePerUser);
        }
    }

}
