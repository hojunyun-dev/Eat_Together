package com.example.eat_together.domain.cart.entity;

import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.users.common.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class SharedCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    private double deliveryFeePerUser; // 🆕 개인 배달팁

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

    public static SharedCartItem of(Menu menu, User user, int quantity) {
        return new SharedCartItem(menu, user, quantity);
    }

    public void setSharedCart(SharedCart sharedCart) {
        this.sharedCart = sharedCart;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return menu.getPrice() * quantity + deliveryFeePerUser;
    }

    public void setDeliveryFeePerUser(double Fee) {
        this.deliveryFeePerUser = Fee;
    }
}
