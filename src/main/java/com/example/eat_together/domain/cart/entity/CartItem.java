package com.example.eat_together.domain.cart.entity;

import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 장바구니에 담긴 단일 메뉴 항목 엔티티
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "cart_items")
public class CartItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    private int quantity;

    /**
     * CartItem 인스턴스 생성
     *
     * @param menu     메뉴 엔티티
     * @param quantity 수량
     * @return 생성된 CartItem 인스턴스
     */
    public static CartItem of(Menu menu, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.menu = menu;
        cartItem.quantity = quantity;
        return cartItem;
    }

    /**
     * 장바구니 연관관계 설정
     *
     * @param cart 장바구니 엔티티
     */
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    /**
     * 수량 변경
     *
     * @param quantity 변경할 수량
     */
    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * 총 가격 계산
     *
     * @return 메뉴 가격 × 수량
     */
    public double getTotalPrice() {
        return menu.getPrice() * quantity;
    }
}
