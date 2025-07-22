package com.example.eat_together.domain.menu.service;

import com.example.eat_together.domain.menu.dto.request.CreateMenuRequestDto;
import com.example.eat_together.domain.menu.dto.respones.MenuResponseDto;
import com.example.eat_together.domain.menu.dto.respones.PagingMenuResponseDto;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.menu.repository.MenuRepository;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.repository.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    public MenuService(MenuRepository menuRepository, StoreRepository storeRepository) {
        this.menuRepository = menuRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional
    public void createMenu(Long storeId, CreateMenuRequestDto requestDto) {

        Store store = storeRepository.findById(storeId).orElseThrow();

        Menu menu = Menu.of(store,
                requestDto.getImageUrl(),
                requestDto.getName(),
                requestDto.getPrice(),
                requestDto.getDescription()
        );

        menuRepository.save(menu);
    }

    @Transactional(readOnly = true)
    public PagingMenuResponseDto getMenusByStoreId(Long storeId, Pageable pageable) {

        Store store = storeRepository.findById(storeId).orElseThrow();

        Pageable menusByStore = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Menu> getMenusByStore = menuRepository.findAllByStore(store, menusByStore);

        return PagingMenuResponseDto.formPage(getMenusByStore);
    }

    public MenuResponseDto getMenuByStore(Long storeId, Long menuId) {

        Store store = storeRepository.findById(storeId).orElseThrow();

        Menu menu = menuRepository.findByMenuIdAndStore(menuId, store);

        MenuResponseDto responseDto =
                new MenuResponseDto(
                        menu.getMenuId(),
                        menu.getImageUrl(),
                        menu.getName(),
                        menu.getDescription(),
                        menu.getPrice(),
                        menu.getCreatedAt(),
                        menu.getUpdatedAt()
                );

        return responseDto;
    }
}
