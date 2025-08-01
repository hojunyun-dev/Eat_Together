package com.example.eat_together.domain.order.fixture;

import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.users.common.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

public class OrderTestFixture {

    public static Order 주문_생성(User user, Store store) {
        Order order = Order.of(
                user,
                store
        );
        ReflectionTestUtils.setField(order, "id", 1L);

        return order;
    }

    public static Order 리스트용_주문_생성(User user, Store store) {
        return Order.of(
                user,
                store
        );
    }
}
