package com.example.eat_together.domain.menu.dto.respones;

import com.example.eat_together.domain.menu.entity.Menu;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PagingMenuResponseDto {

    List<MenuResponseDto> menuList;

    public static PagingMenuResponseDto formPage(Page<Menu> page) {
        List<MenuResponseDto> pagingMenu =
                page
                        .getContent()
                        .stream()
                        .map(Menu -> MenuResponseDto
                                .builder()
                                .menuId(Menu.getMenuId())
                                .imageUrl(Menu.getImageUrl())
                                .name(Menu.getName())
                                .description(Menu.getDescription())
                                .price(Menu.getPrice())
                                .createdAt(Menu.getCreatedAt())
                                .updatedAt(Menu.getUpdatedAt())
                                .build())
                        .toList();

        return PagingMenuResponseDto
                .builder()
                .menuList(pagingMenu)
                .build();
    }

}
