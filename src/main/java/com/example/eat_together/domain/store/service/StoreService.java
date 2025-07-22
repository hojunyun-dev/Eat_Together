package com.example.eat_together.domain.store.service;

import com.example.eat_together.domain.store.dto.request.CreateStoreRequestDto;
import com.example.eat_together.domain.store.dto.response.PagingStoreResponseDto;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.entity.category.FoodCategory;
import com.example.eat_together.domain.store.repository.StoreRepository;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public StoreService(StoreRepository storeRepository, UserRepository userRepository) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createStore(UserDetails userDetails, CreateStoreRequestDto requestDto) {

        String userId = userDetails.getUsername();

        String foodCategory = requestDto.getCategory();
        FoodCategory category = FoodCategory.fromKr(foodCategory);

        User user = userRepository.findByLoginId(userId).orElseThrow();

        Store store = Store.of(user,
                requestDto.getName(),
                requestDto.getDescription(),
                requestDto.getAddress(),
                true,
                requestDto.getOpenTime(),
                requestDto.getCloseTime(),
                requestDto.getDeliveryFee(),
                category,
                requestDto.getPhoneNumber()
        );

        storeRepository.save(store);
    }

    public PagingStoreResponseDto getStoresByCategory(FoodCategory category, Pageable pageable) {

        Pageable storesByCategory = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Store> getStoresByCategory = storeRepository.findByFoodCategoryAndIsDeletedFalse(category, storesByCategory);

        return PagingStoreResponseDto.formPage(getStoresByCategory);
    }
}
