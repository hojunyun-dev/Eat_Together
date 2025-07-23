package com.example.eat_together.domain.store.service;

import com.example.eat_together.domain.store.dto.request.StoreRequestDto;
import com.example.eat_together.domain.store.dto.request.StoreUpdateRequestDto;
import com.example.eat_together.domain.store.dto.response.PagingStoreResponseDto;
import com.example.eat_together.domain.store.dto.response.StoreResponseDto;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.entity.category.FoodCategory;
import com.example.eat_together.domain.store.repository.StoreRepository;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
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
    public void createStore(UserDetails userDetails, StoreRequestDto requestDto) {

        Long userId = Long.valueOf(userDetails.getUsername());

        String foodCategory = requestDto.getCategory();
        FoodCategory category = FoodCategory.fromKr(foodCategory);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


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

    @Transactional(readOnly = true)
    public PagingStoreResponseDto getStoresByCategory(FoodCategory category, Pageable pageable) {

        Pageable storesByCategory = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Store> getStoresByCategory = storeRepository.findByFoodCategoryAndIsDeletedFalse(category, storesByCategory);

        return PagingStoreResponseDto.formPage(getStoresByCategory);
    }

    @Transactional(readOnly = true)
    public PagingStoreResponseDto getStoresByUserId(UserDetails userDetails, Pageable pageable) {

        Long userId = Long.valueOf(userDetails.getUsername());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Pageable storesByUserId = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Store> response = storeRepository.findStoresByUserAndIsDeletedFalse(user, storesByUserId);

        return PagingStoreResponseDto.formPage(response);
    }

    @Transactional(readOnly = true)
    public StoreResponseDto getStore(Long storeId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        return StoreResponseDto.from(store);
    }

    @Transactional(readOnly = true)
    public PagingStoreResponseDto getStoreBySearch(String keyword, Pageable pageable) {

        Pageable bySearch = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Store> response = storeRepository.findBySearch(keyword, bySearch);

        return PagingStoreResponseDto.formPage(response);
    }

    @Transactional
    public StoreResponseDto updateStore(Long storeId, StoreUpdateRequestDto request) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        boolean isUpdated = false;

        if (request.getName() != null && !request.getName().equals(store.getName())) {
            store.updateName(request.getName());
            isUpdated = true;
        }

        if (request.getDescription() != null && !request.getDescription().equals(store.getDescription())) {
            store.updateDescription(request.getDescription());
            isUpdated = true;
        }

        if (request.getAddress() != null && !request.getAddress().equals(store.getAddress())) {
            store.updateAddress(request.getAddress());
            isUpdated = true;
        }

        if (request.getOpenTime() != null && !request.getOpenTime().equals(store.getOpenTime())) {
            store.updateOpenTime(request.getOpenTime());
            isUpdated = true;
        }

        if (request.getCloseTime() != null && !request.getCloseTime().equals(store.getCloseTime())) {
            store.updateCloseTime(request.getCloseTime());
            isUpdated = true;
        }

        if (request.getDeliveryFee() != null &&
                Double.compare(request.getDeliveryFee(), store.getDeliveryFee()) != 0) {
            store.updateDeliveryFee(request.getDeliveryFee());
            isUpdated = true;
        }

        if (request.getCategory() != null) {
            FoodCategory category = FoodCategory.fromKr(request.getCategory());
            if (!category.equals(store.getFoodCategory())) {
                store.updateFoodCategory(category);
                isUpdated = true;
            }
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(store.getPhoneNumber())) {
            store.updatePhoneNumber(request.getPhoneNumber());
            isUpdated = true;
        }

        if (!isUpdated) {
            throw new CustomException(ErrorCode.UPDATE_CONTENT_REQUIRED);
        }

        return StoreResponseDto.from(store);
    }
}
