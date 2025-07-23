package com.example.eat_together.domain.order.entity;

import com.example.eat_together.domain.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    private int quantity;

    private int price;

    public static OrderItem of(Order order, Menu menu, int quantity, int price) {
        OrderItem orderItem = new OrderItem();
        orderItem.order = order;
        orderItem.menu = menu;
        orderItem.quantity = quantity;
        orderItem.price = price;
        return orderItem;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
