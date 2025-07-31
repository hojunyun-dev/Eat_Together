package com.example.eat_together.domain.cart.fixture;

import com.example.eat_together.domain.cart.entity.Cart;
import com.example.eat_together.domain.cart.entity.CartItem;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.users.common.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

public class CartTestFixture {

    public static CartItem 장바구니_아이템_생성(Menu menu, int quantity) {
        CartItem cartItem = CartItem.of(
                menu,
                quantity
        );
        ReflectionTestUtils.setField(cartItem, "id", 1L);

        return cartItem;
    }

    public static Cart 장바구니_생성(User user) {
        Cart cart = Cart.of(
                user
        );
        ReflectionTestUtils.setField(cart, "id", 1L);

        return cart;
    }
}
