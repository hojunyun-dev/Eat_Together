package com.example.eat_together.domain.cart.entity;

import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.users.common.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공유 장바구니에 담긴 단일 메뉴 항목 엔티티
 */
@Entity
@Getter
@NoArgsConstructor
public class SharedCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    private double deliveryFeePerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_cart_id")
    private SharedCart sharedCart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private SharedCartItem(Menu menu, User user, int quantity) {
        this.menu = menu;
        this.user = user;
        this.quantity = quantity;
    }

    /**
     * SharedCartItem 인스턴스 생성
     *
     * @param menu     메뉴 엔티티
     * @param user     사용자 엔티티
     * @param quantity 수량
     * @return 생성된 SharedCartItem 인스턴스
     */
    public static SharedCartItem of(Menu menu, User user, int quantity) {
        return new SharedCartItem(menu, user, quantity);
    }

    /**
     * 공유 장바구니 연관관계 설정
     *
     * @param sharedCart 공유 장바구니 엔티티
     */
    public void setSharedCart(SharedCart sharedCart) {
        this.sharedCart = sharedCart;
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
     * 총 가격 계산 (메뉴 가격 × 수량 + 개인 배달팁)
     *
     * @return 총 가격
     */
    public double getTotalPrice() {
        return menu.getPrice() * quantity + deliveryFeePerUser;
    }

    /**
     * 개인 배달팁 변경
     *
     * @param fee 변경할 배달팁 금액
     */
    public void setDeliveryFeePerUser(double fee) {
        this.deliveryFeePerUser = fee;
    }
}