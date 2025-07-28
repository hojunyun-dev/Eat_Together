package com.example.eat_together.domain.menu.service;

import com.example.eat_together.domain.menu.dto.request.MenuRequestDto;
import com.example.eat_together.domain.menu.dto.request.MenuUpdateRequestDto;
import com.example.eat_together.domain.menu.dto.respones.MenuResponseDto;
import com.example.eat_together.domain.menu.dto.respones.PagingMenuResponseDto;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.menu.repository.MenuRepository;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.repository.StoreRepository;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, PagingMenuResponseDto> pagingMenuRedisTemplate;
    private final RedisTemplate<String, MenuResponseDto> menuRedisTemplate;

    public MenuService(MenuRepository menuRepository, StoreRepository storeRepository, UserRepository userRepository, RedisTemplate<String, PagingMenuResponseDto> pagingMenuRedisTemplate, RedisTemplate<String, MenuResponseDto> menuRedisTemplate) {
        this.menuRepository = menuRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.pagingMenuRedisTemplate = pagingMenuRedisTemplate;
        this.menuRedisTemplate = menuRedisTemplate;
    }

    @Transactional
    public void createMenu(Long storeId, MenuRequestDto requestDto, UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByStoreIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (!user.getUserId().equals(store.getUser().getUserId())) {
            throw new CustomException(ErrorCode.STORE_ACCESS_DENIED);
        }

        if (menuRepository.existsByStoreAndNameAndIsDeletedFalse(store, requestDto.getName())) {
            throw new CustomException(ErrorCode.MENU_NAME_DUPLICATED);
        }

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
        String cacheKey = "storeMenus:" + storeId;

        PagingMenuResponseDto cache = pagingMenuRedisTemplate.opsForValue().get(cacheKey);

        // 캐싱된 키가 존재할 시 즉시 캐시 데이터 반환
        if (cache != null) {
            return cache;
        }

        Store store = storeRepository.findByStoreIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        Pageable menusByStore = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Menu> getMenusByStore = menuRepository.findAllByStoreAndIsDeletedFalse(store, menusByStore);

        PagingMenuResponseDto responseDto = PagingMenuResponseDto.formPage(getMenusByStore);
        pagingMenuRedisTemplate.opsForValue().set(cacheKey, responseDto);

        return responseDto;
    }

    @Transactional(readOnly = true)
    public MenuResponseDto getMenuByStore(Long storeId, Long menuId) {
        String cacheKey = "menu:" + menuId;

        MenuResponseDto cache = menuRedisTemplate.opsForValue().get(cacheKey);

        // 캐싱된 키가 존재할 시 즉시 캐시 데이터 반환
        if (cache != null) {
            return cache;
        }

        Store store = storeRepository.findByStoreIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        Menu menu = menuRepository.findByMenuIdAndStore(menuId, store);
        if (menu == null) {
            throw new CustomException(ErrorCode.MENU_NOT_FOUND);
        }

        MenuResponseDto responseDto = MenuResponseDto.from(menu);
        menuRedisTemplate.opsForValue().set(cacheKey, responseDto);

        return responseDto;
    }

    @Transactional
    public MenuResponseDto updateMenu(Long storeId, Long menuId, MenuUpdateRequestDto request, UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByStoreIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (!user.getUserId().equals(store.getUser().getUserId())) {
            throw new CustomException(ErrorCode.STORE_ACCESS_DENIED);
        }

        Menu menu = menuRepository.findByMenuIdAndStore(menuId, store);

        if (menu == null) {
            throw new CustomException(ErrorCode.MENU_NOT_FOUND);
        }

        boolean isUpdated = false;

        if (request.getImageUrl() != null && !request.getImageUrl().equals(menu.getImageUrl())) {
            menu.updateImageUrl(request.getImageUrl());
            isUpdated = true;
        }

        if (request.getName() != null && !request.getName().equals(menu.getName())) {
            menu.updateName(request.getName());
            isUpdated = true;
        }

        if (request.getDescription() != null && !request.getDescription().equals(menu.getDescription())) {
            menu.updateDescription(request.getDescription());
            isUpdated = true;
        }

        if (request.getPrice() != null && !request.getPrice().equals(menu.getPrice())) {
            menu.updatePrice(request.getPrice());
            isUpdated = true;
        }

        if (!isUpdated) {
            throw new CustomException(ErrorCode.UPDATE_CONTENT_REQUIRED);
        }

        return MenuResponseDto.from(menu);
    }

    @Transactional
    public void deleteMenu(Long storeId, Long menuId, UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByStoreIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (!user.getUserId().equals(store.getUser().getUserId())) {
            throw new CustomException(ErrorCode.STORE_ACCESS_DENIED);
        }

        Menu menu = menuRepository.findByMenuIdAndStore(menuId, store);

        if (menu == null) {
            throw new CustomException(ErrorCode.MENU_NOT_FOUND);
        }

        menu.deleted();
    }
}
