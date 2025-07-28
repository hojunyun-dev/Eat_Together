package com.example.eat_together.domain.order.service;

import com.example.eat_together.domain.cart.entity.Cart;
import com.example.eat_together.domain.cart.entity.CartItem;
import com.example.eat_together.domain.cart.repository.CartRepository;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.order.dto.response.OrderDetailResponseDto;
import com.example.eat_together.domain.order.dto.response.OrderResponseDto;
import com.example.eat_together.domain.order.dto.response.OrderStatusUpdateResponseDto;
import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.order.orderEnum.OrderStatus;
import com.example.eat_together.domain.order.repository.OrderRepository;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.entity.UserRole;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.example.eat_together.domain.store.entity.category.FoodCategory.KOREAN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;


    private User user;
    private Store store;
    private Menu menu1;
    private Menu menu2;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = User.createAuth(
                "test1",
                "테스트 이름",
                "!1Password",
                "test@example.com",
                "테스트 닉네임");
        ReflectionTestUtils.setField(user, "userId", 1L);

        store = Store.of(user,
                "테스트 가게 이름",
                "테스트 가게 설명",
                "테스트 가게 주소",
                true,
                LocalTime.of(9, 0),
                LocalTime.of(21, 0),
                3000.0, KOREAN,
                "010-1234-1234");
        ReflectionTestUtils.setField(store, "storeId", 1L);

        menu1 = Menu.of(store,
                "https://i.namu.wiki/i/abZPxKt_L98I8ttqw56pLHtGiR5pAV4YYmpR3Ny3_n0yvff5IDoKEQFof7EbzJUSZ_-uzR5S7tzTzGQ346Qixw.webp",
                "테스트 메뉴 이름1",
                5000.0,
                "테스트 메뉴 설명1");
        menu2 = Menu.of(store,
                "https://i.namu.wiki/i/abZPxKt_L98I8ttqw56pLHtGiR5pAV4YYmpR3Ny3_n0yvff5IDoKEQFof7EbzJUSZ_-uzR5S7tzTzGQ346Qixw.webp",
                "테스트 메뉴 이름2",
                6000.0,
                "테스트 메뉴 설명2");

        CartItem cartItem1 = CartItem.of(menu1, 2);
        CartItem cartItem2 = CartItem.of(menu2, 3);

        cart = Cart.of(user);
        cart.addCartItem(cartItem1);
        cart.addCartItem(cartItem2);
    }

    @Test
    @DisplayName("주문 성공")
    void createOrder_Success() {
        // given
        given(cartRepository.findByUserUserId(user.getUserId())).willReturn(Optional.of(cart));
        given(orderRepository.findByUserIdAndStoreIdAndStatus(user.getUserId(), store.getStoreId(), OrderStatus.ORDERED)).willReturn(List.of());

        // when & then
        assertDoesNotThrow(() -> orderService.createOrder(user.getUserId()));
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class); // ArgumentCaptor : 메서드가 호출 될 때 어떤 값이 전달됐는지 캡쳐해서 가져오는 것
        verify(orderRepository).save(orderCaptor.capture()); // save 호출 시 전달된 값을 캡쳐해서 검사

        Order savedOrder = orderCaptor.getValue();
        assertEquals(cart.getCartItems().size(), savedOrder.getOrderItems().size()); // 장바구니 아이템과 주문 아이템 수가 같은지 확인
    }

    @Test
    @DisplayName("주문 실패 - 장바구니가 없는 경우")
    void createOrder_Fail_WhenCartIsEmpty() {
        // given
        given(cartRepository.findByUserUserId(user.getUserId())).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> orderService.createOrder(user.getUserId()));
        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("주문 실패 - 장바구니가 비어있는 경우")
    void createOrder_Fail_WhenCartItemIsEmpty() {
        // given
        Cart emptyCart = Cart.of(user);
        given(cartRepository.findByUserUserId(user.getUserId())).willReturn(Optional.of(emptyCart));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> orderService.createOrder(user.getUserId()));
        assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("주문 실패 - 중복 주문인 경우")
    void createOrder_Fail_WhenOrderIsDuplicated() {
        // given
        given(cartRepository.findByUserUserId(user.getUserId())).willReturn(Optional.of(cart));

        List<Order> existingOrder = List.of(Order.of(user, store));
        given(orderRepository.findByUserIdAndStoreIdAndStatus(user.getUserId(), store.getStoreId(), OrderStatus.ORDERED)).willReturn(existingOrder);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> orderService.createOrder(user.getUserId()));
        assertEquals(ErrorCode.DUPLICATE_ORDER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("주문 내역 목록 조회 성공")
    void getOrders_Success() {
        // given
        Order order1 = Order.of(user, store);
        Order order2 = Order.of(user, store);
        ReflectionTestUtils.setField(order1, "id", 1L);
        ReflectionTestUtils.setField(order2, "id", 2L);

        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);

        OrderResponseDto dto1 = new OrderResponseDto(order1);
        OrderResponseDto dto2 = new OrderResponseDto(order2);

        List<OrderResponseDto> orderDtoList = List.of(dto1, dto2);
        Page<OrderResponseDto> page = new PageImpl<>(orderDtoList);

        given(orderRepository.findOrdersByUserId(eq(user.getUserId()), any(Pageable.class), any(), any(), any())).willReturn(page);

        // when
        Page<OrderResponseDto> response = orderService.getOrders(user.getUserId(), 1, 10, startDate, endDate, OrderStatus.ORDERED);

        // then
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(dto1.getId(), response.getContent().get(0).getId());
        assertEquals(dto2.getId(), response.getContent().get(1).getId());
    }

    @Test
    @DisplayName("주문 내역 목록 조회 성공 - 날짜 파라미터 없는 경우")
    void getOrders_Success_WhenNullStartDateAndEndDate() {
        // given
        Order order1 = Order.of(user, store);
        Order order2 = Order.of(user, store);
        ReflectionTestUtils.setField(order1, "id", 1L);
        ReflectionTestUtils.setField(order2, "id", 2L);

        OrderResponseDto dto1 = new OrderResponseDto(order1);
        OrderResponseDto dto2 = new OrderResponseDto(order2);

        List<OrderResponseDto> orderDtoList = List.of(dto1, dto2);
        Page<OrderResponseDto> page = new PageImpl<>(orderDtoList);

        given(orderRepository.findOrdersByUserId(eq(user.getUserId()), any(Pageable.class), any(), any(), any())).willReturn(page);

        // when
        Page<OrderResponseDto> response = orderService.getOrders(user.getUserId(), 1, 10, null, null, OrderStatus.ORDERED);

        // then
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(dto1.getId(), response.getContent().get(0).getId());
        assertEquals(dto2.getId(), response.getContent().get(1).getId());
    }

    @Test
    @DisplayName("주문 내역 목록 조회 실패 - 조회 시작일과 종료일 중에 하나만 입력한 경우")
    void getOrders_Fail_WhenStartDateOrEndDateIsEmpty() {
        // given
        LocalDate startDate1 = LocalDate.of(2025, 7, 1);
        LocalDate endDate1 = null;

        // when & then
        CustomException exception1 = assertThrows(CustomException.class, () -> orderService.getOrders(user.getUserId(), 1, 10, startDate1, endDate1, OrderStatus.ORDERED));
        assertEquals(ErrorCode.ORDER_PERIOD_MISMATCH.getMessage(), exception1.getMessage());

        // given
        LocalDate startDate2 = null;
        LocalDate endDate2 = LocalDate.of(2025, 7, 31);

        // when & then
        CustomException exception2 = assertThrows(CustomException.class, () -> orderService.getOrders(user.getUserId(), 1, 10, startDate2, endDate2, OrderStatus.ORDERED));
        assertEquals(ErrorCode.ORDER_PERIOD_MISMATCH.getMessage(), exception2.getMessage());
    }

    @Test
    @DisplayName("주문 내역 목록 조회 실패 - 조회 시작일이 종료일보다 늦은 경우")
    void getOrders_Fail_WhenEndDateIsBeforeStartDate() {
        // given
        LocalDate startDate1 = LocalDate.of(2025, 7, 1);
        LocalDate endDate1 = LocalDate.of(2025, 6, 1);

        // when & then
        CustomException exception1 = assertThrows(CustomException.class, () -> orderService.getOrders(user.getUserId(), 1, 10, startDate1, endDate1, OrderStatus.ORDERED));
        assertEquals(ErrorCode.ORDER_INVALID_PERIOD.getMessage(), exception1.getMessage());
    }

    @Test
    @DisplayName("주문 내역 단건 조회 성공")
    void getOrder_Success() {
        // given
        Order order = Order.of(user, store);
        ReflectionTestUtils.setField(order, "id", 1L);
        given(userRepository.findById(user.getUserId())).willReturn(Optional.of(user));
        given(orderRepository.findByIdAndUserId(order.getId(), user.getUserId())).willReturn(Optional.of(order));

        // when
        OrderDetailResponseDto response = orderService.getOrder(order.getId(), user.getUserId());

        // then
        assertNotNull(response);
        assertEquals(order.getId(), response.getId());
        assertEquals(order.getUser().getUserId(), response.getUserId());
    }

    @Test
    @DisplayName("주문 상태 변경 성공")
    void updateOrderStatus_Success() {
        // given
        ReflectionTestUtils.setField(user, "role", UserRole.OWNER); // 권한을 owner 로 설정
        Order order = Order.of(user, store);
        ReflectionTestUtils.setField(order, "id", 1L);

        given(userRepository.findById(user.getUserId())).willReturn(Optional.of(user));
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when
        OrderStatusUpdateResponseDto response = orderService.updateOrderStatus(user.getUserId(), order.getId(), OrderStatus.DELIVERING);

        // then
        assertNotNull(response);
        assertEquals(OrderStatus.DELIVERING, response.getStatus());
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - 권한이 없는 경우")
    void updateOrderStatus_Fail_WhenUserIsNotOwner() {
        // given
        Order order = Order.of(user, store);
        ReflectionTestUtils.setField(order, "id", 1L);

        given(userRepository.findById(user.getUserId())).willReturn(Optional.of(user));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> orderService.updateOrderStatus(user.getUserId(), order.getId(), OrderStatus.DELIVERING));
        assertEquals(ErrorCode.FORBIDDEN_ACCESS.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - 가게 오너 Id와 요청한 사용자 Id가 다른 경우")
    void updateOrderStatus_Fail_WhenUserIsNotStoreOwner() {
        // given
        ReflectionTestUtils.setField(user, "role", UserRole.OWNER);
        Order order = Order.of(user, store);
        ReflectionTestUtils.setField(order, "id", 1L);

        User user2 = User.createAuth("owner2", "다른 오너", "!1Password", "owner2@example.com", "다른 오너닉네임");
        ReflectionTestUtils.setField(user2, "userId", 5L);

        Store store2 = Store.of(user2, "다른 가게", "설명", "주소", true,
                LocalTime.of(9, 0), LocalTime.of(21, 0), 3000.0, KOREAN, "010-9999-9999");
        ReflectionTestUtils.setField(store2, "storeId", 5L);

        Order order2 = Order.of(user2, store2);
        ReflectionTestUtils.setField(user2, "role", UserRole.OWNER);
        ReflectionTestUtils.setField(order2, "id", 1L);

        given(userRepository.findById(user.getUserId())).willReturn(Optional.of(user));
        given(orderRepository.findById(order2.getId())).willReturn(Optional.of(order2));


        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> orderService.updateOrderStatus(user.getUserId(), order2.getId(), OrderStatus.DELIVERING));
        assertEquals(ErrorCode.FORBIDDEN_ACCESS.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("주문 내역 단건 삭제(소프트 딜리트)")
    void deleterOrder_Success() {
        // given
        Order order = Order.of(user, store);
        given(userRepository.findById(user.getUserId())).willReturn(Optional.of(user));
        given(orderRepository.findByIdAndUserId(order.getId(), user.getUserId())).willReturn(Optional.of(order));

        // when
        assertDoesNotThrow(() -> orderService.deleteOrder(user.getUserId(), order.getId()));

        // then
        assertTrue(order.isDeleted());
    }
}
