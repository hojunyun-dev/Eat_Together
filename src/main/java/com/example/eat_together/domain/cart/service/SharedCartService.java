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
import com.example.eat_together.domain.chat.repository.ChatRoomUserRepository;
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

/**
 * 공유 장바구니 도메인의 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class SharedCartService {

    private final SharedCartRepository sharedCartRepository;
    private final SharedCartItemRepository sharedCartItemRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    /**
     * 사용자가 채팅방의 멤버인지 검증
     *
     * @param userId 사용자 ID
     * @param roomId 채팅방 ID
     */
    private void assertMember(Long userId, Long roomId) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM);
        }
        if (!chatRoomUserRepository.existsByUserUserIdAndChatRoomId(userId, roomId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    /**
     * 공유 장바구니에 항목 추가
     *
     * @param userId     사용자 ID
     * @param roomId     채팅방 ID
     * @param storeId    매장 ID
     * @param requestDto 메뉴 및 수량 정보
     */
    @Transactional
    public void addItem(Long userId, Long roomId, Long storeId, SharedCartItemRequestDto requestDto) {
        assertMember(userId, roomId);

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
    }

    /**
     * 공유 장바구니 항목 전체 조회
     *
     * @param userId 사용자 ID
     * @param roomId 채팅방 ID
     * @return 공유 장바구니 응답 DTO
     */
    @Transactional(readOnly = true)
    public SharedCartResponseDto getSharedCartItems(Long userId, Long roomId) {
        assertMember(userId, roomId);

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

    /**
     * 공유 장바구니 항목 수량 수정
     *
     * @param userId     사용자 ID
     * @param roomId     채팅방 ID
     * @param itemId     항목 ID
     * @param requestDto 변경할 수량 정보
     */
    @Transactional
    public void updateQuantity(Long userId, Long roomId, Long itemId, SharedCartItemRequestDto requestDto) {
        assertMember(userId, roomId);

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
     *
     * @param userId 사용자 ID
     * @param roomId 채팅방 ID
     * @param itemId 항목 ID
     */
    @Transactional
    public void deleteItem(Long userId, Long roomId, Long itemId) {
        assertMember(userId, roomId);

        SharedCartItem item = sharedCartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!item.getUser().getUserId().equals(userId)
                || !item.getSharedCart().getChatRoom().getId().equals(roomId)) {
            throw new CustomException(ErrorCode.SHARED_CART_ITEM_ACCESS_DENIED);
        }

        sharedCartItemRepository.delete(item);
    }
}