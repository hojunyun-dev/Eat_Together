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

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String description;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public static Menu of(Store store,
                          String imageUrl,
                          String name,
                          double price,
                          String description
    ) {
        Menu menu = new Menu();
        menu.store = store;
        menu.imageUrl = imageUrl;
        menu.name = name;
        menu.price = price;
        menu.description = description;
        return menu;
    }


    public void update(String imageUrl, String name, String description, double price) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updatePrice(double price) {
        this.price = price;
    }


    // 메뉴 삭제 시 사용
    public void deleted() {
        this.isDeleted = true;
    }

    // 메뉴 삭제를 되돌릴 때 사용
    public void returnDeleted() {
        this.isDeleted = false;
    }

}
