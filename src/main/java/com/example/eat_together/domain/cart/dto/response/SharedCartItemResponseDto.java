package com.example.eat_together.domain.cart.dto.response;

import com.example.eat_together.domain.cart.entity.SharedCartItem;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
        "userName",
        "itemId",
        "menuId",
        "menuName",
        "quantity",
        "price",
        "deliveryFeePerUser",
        "totalPrice"
})
public class SharedCartItemResponseDto {

    private final Long itemId;
    private final Long menuId;
    private final String menuName;
    private final int quantity;
    private final double price;
    private final String userName;
    private final double deliveryFeePerUser;
    private final double totalPrice;

    @Builder
    private SharedCartItemResponseDto(String userName, Long itemId, Long menuId, String menuName, int quantity, double price,
                                      double deliveryFeePerUser, double totalPrice) {
        this.userName = userName;
        this.itemId = itemId;
        this.menuId = menuId;
        this.menuName = menuName;
        this.quantity = quantity;
        this.price = price;
        this.deliveryFeePerUser = deliveryFeePerUser;
        this.totalPrice = totalPrice;
    }

    public static SharedCartItemResponseDto from(SharedCartItem item, double deliveryFeePerUser) {
        double total = item.getMenu().getPrice() * item.getQuantity() + deliveryFeePerUser;
        return SharedCartItemResponseDto.builder()
                .userName(item.getUser().getNickname())
                .itemId(item.getId())
                .menuId(item.getMenu().getMenuId())
                .menuName(item.getMenu().getName())
                .quantity(item.getQuantity())
                .price(item.getMenu().getPrice())
                .deliveryFeePerUser(deliveryFeePerUser)
                .totalPrice(total)
                .build();
    }
}
