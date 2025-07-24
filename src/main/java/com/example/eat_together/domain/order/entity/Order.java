package com.example.eat_together.domain.order.entity;

import com.example.eat_together.domain.order.orderEnum.OrderStatus;
import com.example.eat_together.domain.rider.entity.Rider;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private double totalPrice;

    @Column(nullable = false)
    private boolean isDeleted;

    public static Order of(User user, Store store) {
        Order order = new Order();
        order.user = user;
        order.store = store;
        order.status = OrderStatus.ORDERED;
        order.isDeleted = false;
        return order;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);      // Order 입장에서 자식 추가
        orderItem.setOrder(this);       // OrderItem 입장에서 부모 참조 설정
    }

    public void calculateTotalPrice() {
        double total = 0;
        for (OrderItem orderItem : orderItems) {
            total += orderItem.getPrice() * orderItem.getQuantity();
        }
        total += store.getDeliveryFee();
        this.totalPrice = total;
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void deletedOrder() {
        this.isDeleted = true;
    }

}
