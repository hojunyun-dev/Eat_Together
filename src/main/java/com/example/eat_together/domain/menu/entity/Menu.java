package com.example.eat_together.domain.menu.entity;

import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "menus")
@Getter
public class Menu extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String description;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    public static Menu of(Store store,
                          String name,
                          double price,
                          String description,
                          boolean isDeleted
    ) {
        Menu menu = new Menu();
        menu.store = store;
        menu.name = name;
        menu.price = price;
        menu.description = description;
        menu.isDeleted = isDeleted;
        return menu;
    }

}
