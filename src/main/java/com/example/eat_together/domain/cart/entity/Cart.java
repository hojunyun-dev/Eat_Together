package com.example.eat_together.domain.cart.entity;

import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 장바구니를 나타내는 엔티티
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "carts")
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    /**
     * Cart 생성 정적 팩토리 메서드
     *
     * @param user 사용자 엔티티
     * @return 생성된 Cart 인스턴스
     */
    public static Cart of(User user) {
        Cart cart = new Cart();
        cart.user = user;
        return cart;
    }

    /**
     * 장바구니 항목 추가
     *
     * @param cartItem 추가할 항목
     */
    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    /**
     * 장바구니 항목 전체 비우기
     */
    public void clearItems() {
        cartItems.clear();
    }
}
