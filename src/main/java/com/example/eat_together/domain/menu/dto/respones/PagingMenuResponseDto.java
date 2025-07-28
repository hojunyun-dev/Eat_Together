package com.example.eat_together.domain.menu.dto.respones;

import com.example.eat_together.domain.menu.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagingMenuResponseDto {

    List<MenuResponseDto> menuList;

    public static PagingMenuResponseDto formPage(Page<Menu> page) {
        List<MenuResponseDto> pagingMenu =
                page
                        .getContent()
                        .stream()
                        .map(MenuResponseDto::from)
                        .toList();

        return PagingMenuResponseDto
                .builder()
                .menuList(pagingMenu)
                .build();
    }

}
