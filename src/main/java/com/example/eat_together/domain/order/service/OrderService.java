package com.example.eat_together.domain.order.service;

import com.example.eat_together.domain.cart.entity.Cart;
import com.example.eat_together.domain.cart.entity.CartItem;
import com.example.eat_together.domain.cart.entity.SharedCart;
import com.example.eat_together.domain.cart.entity.SharedCartItem;
import com.example.eat_together.domain.cart.repository.CartRepository;
import com.example.eat_together.domain.cart.repository.SharedCartRepository;
import com.example.eat_together.domain.chat.chatEnum.MemberRole;
import com.example.eat_together.domain.chat.entity.ChatRoom;
import com.example.eat_together.domain.chat.entity.ChatRoomUser;
import com.example.eat_together.domain.order.dto.response.OrderDetailResponseDto;
import com.example.eat_together.domain.order.dto.response.OrderResponseDto;
import com.example.eat_together.domain.order.dto.response.OrderStatusUpdateResponseDto;
import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.order.entity.OrderItem;
import com.example.eat_together.domain.order.orderEnum.OrderStatus;
import com.example.eat_together.domain.order.repository.OrderCacheRepository;
import com.example.eat_together.domain.order.repository.OrderRepository;
import com.example.eat_together.domain.payment.entity.Payment;
import com.example.eat_together.domain.payment.repository.PaymentRepository;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.users.common.entity.User;
import com.example.eat_together.domain.users.common.enums.UserRole;
import com.example.eat_together.domain.users.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderCacheRepository orderCacheRepository;
    private final PaymentRepository paymentRepository;
    private final SharedCartRepository sharedCartRepository;

    // 주문 생성 (개인 장바구니)
    @Transactional
    public void createOrder(Long userId) {

        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        User user = cart.getUser();

        if (cart.getCartItems().isEmpty()) {
            throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        Store store = cart.getCartItems().get(0).getMenu().getStore();

        // 주문완료상태인 주문이 같은 가게에 있으면 중복 주문으로 간주 (JPA 비관적락 적용)
        List<Order> orderWithOrdered =
                orderRepository.findByUserIdAndStoreIdAndStatus(userId, store.getStoreId(), OrderStatus.ORDERED);
        if (!orderWithOrdered.isEmpty()) {
            throw new CustomException(ErrorCode.DUPLICATE_ORDER);
        }

        Order order = Order.of(user, store);

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = OrderItem.of(order, cartItem.getMenu(), cartItem.getQuantity());
            order.addOrderItem(orderItem);
        }

        order.calculateTotalPrice();

        orderRepository.save(order);

        Payment payment = Payment.of(order);
        paymentRepository.save(payment);
    }

    // 주문 생성 (공유 장바구니)
    @Transactional
    public void createSharedOrder(Long userId, Long chatRoomId) {
        // 장바구니가 있는지 조회
        SharedCart sharedCart = sharedCartRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        // 방장인지 확인
        ChatRoom chatRoom = sharedCart.getChatRoom();
        User host = chatRoom.getChatRoomUserList().stream()
                .filter(cru -> cru.getMemberRole() == MemberRole.HOST)
                .map(ChatRoomUser::getUser)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.NO_CHATROOM_LEADER));

        if (!host.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }


        if (sharedCart.getItems().isEmpty()) {
            throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        // 채팅에 참여한 모든 멤버가 장바구니에 메뉴를 1개 이상 넣었는지 확인
        Set<User> usersWithItems = sharedCart.getItems().stream()
                .map(SharedCartItem::getUser)
                .collect(Collectors.toSet());

        for (ChatRoomUser cru : chatRoom.getChatRoomUserList()) {
            if (!usersWithItems.contains(cru.getUser())) {
                throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
            }
        }

        // 참여자 목록 생성 및 가게 정보 조회
        Store store = sharedCart.getItems().get(0).getMenu().getStore();

        List<User> participants = sharedCart.getItems().stream()
                .map(SharedCartItem::getUser)
                .distinct()
                .toList();

        // 배송비 계산 및 설정
        double deliveryFeePerUser = store.getDeliveryFee() / participants.size();

        // 참여자별 주문 생성
        for (User participant : participants) {
            // 중복 주문 체크
            List<Order> orderWithOrdered = orderRepository.findByUserIdAndStoreIdAndStatus(participant.getUserId(), store.getStoreId(), OrderStatus.ORDERED);
            if (!orderWithOrdered.isEmpty()) {
                throw new CustomException(ErrorCode.DUPLICATE_ORDER);
            }

            Order order = Order.of(participant, store);

            boolean firstItem = true;
            for (SharedCartItem item : sharedCart.getItems()) {
                if (item.getUser().equals(participant)) {
                    OrderItem orderItem = OrderItem.of(order, item.getMenu(), item.getQuantity());
                    order.addOrderItem(orderItem);

                    // 배송비는 첫 아이템에만 할당
                    if (firstItem) {
                        item.setDeliveryFeePerUser(deliveryFeePerUser);
                        firstItem = false;
                    } else {
                        item.setDeliveryFeePerUser(0.0);
                    }
                }
            }

            order.calculateSharedTotalPrice(deliveryFeePerUser);
            orderRepository.save(order);

            Payment payment = Payment.of(order);
            paymentRepository.save(payment);
        }
    }

    // 주문 목록 조회
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrders(Long userId, int page, int size, String menuName, String storeName, LocalDate startDate, LocalDate endDate, OrderStatus status) {
        // 페이지 사이즈를 최대 50으로 지정
        int maxSize = 50;
        size = Math.min(size, maxSize);

        Pageable pageable = PageRequest.of(page - 1, size);

        // 조회 시작일과 종료일 중에 하나만 입력했을 경우 예외 발생
        if ((startDate != null && endDate == null) || (startDate == null && endDate != null)) {
            throw new CustomException(ErrorCode.ORDER_PERIOD_MISMATCH);
        }

        // 조회 시작일이 종료일보다 늦은 경우 예외 발생
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new CustomException(ErrorCode.ORDER_INVALID_PERIOD);
        }
        return orderRepository.findOrdersByUserId(userId, pageable, menuName, storeName, startDate, endDate, status);
    }

    // 주문 목록 단일 조회
    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrder(Long userId, Long orderId) {
        // 캐시가 존재하면 캐시 조회
        OrderDetailResponseDto cached = orderCacheRepository.getOrder(userId, orderId);
        if (cached != null) {
            return cached;
        }

        // 캐시가 존재하지 않으면 DB 조회 후 캐시에 저장
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Order order = orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        OrderDetailResponseDto response = new OrderDetailResponseDto(order);
        orderCacheRepository.saveOrderCache(userId, orderId, response);
        return response;
    }

    // 주문 상태 변경(가게 권한)
    @Transactional
    public OrderStatusUpdateResponseDto updateOrderStatus(Long userId, Long orderId, OrderStatus status) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException((ErrorCode.USER_NOT_FOUND)));

        if (user.getRole() != UserRole.OWNER) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // 가게 오너 Id와 요청한 사용자 Id가 다를 경우 예외 발생
        if (!order.getStore().getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        order.updateStatus(status);
        orderRepository.save(order);

        OrderDetailResponseDto updatedDto = new OrderDetailResponseDto(order);

        // 주문 상태 변경 후 캐시에 저장
        orderCacheRepository.saveOrderCache(userId, orderId, updatedDto);

        return new OrderStatusUpdateResponseDto(order);
    }

    // 주문 단건 삭제 (소프트 딜리트)
    @Transactional
    public void deleteOrder(Long userId, Long orderId) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Order order = orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // 캐시 삭제
        orderCacheRepository.evictOrder(userId, orderId);

        order.deletedOrder();
    }


}
