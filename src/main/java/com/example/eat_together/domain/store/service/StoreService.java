package com.example.eat_together.domain.store.service;

import com.example.eat_together.domain.store.dto.request.StoreRequestDto;
import com.example.eat_together.domain.store.dto.request.StoreUpdateRequestDto;
import com.example.eat_together.domain.store.dto.response.PagingStoreResponseDto;
import com.example.eat_together.domain.store.dto.response.StoreResponseDto;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.entity.category.FoodCategory;
import com.example.eat_together.domain.store.repository.StoreRepository;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.entity.UserRole;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.dto.TokenResponse;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.util.JwtUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, PagingStoreResponseDto> pagingStoreRedisTemplate;
    private final RedisTemplate<String, StoreResponseDto> storeRedisTemplate;

    public StoreService(StoreRepository storeRepository, UserRepository userRepository, JwtUtil jwtUtil, StringRedisTemplate stringRedisTemplate, RedisTemplate<String, PagingStoreResponseDto> pagingStoreRedisTemplate, RedisTemplate<String, StoreResponseDto> storeRedisTemplate) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
        this.pagingStoreRedisTemplate = pagingStoreRedisTemplate;
        this.storeRedisTemplate = storeRedisTemplate;
    }

    @Transactional
    public TokenResponse createStore(UserDetails userDetails, StoreRequestDto requestDto) {

        LocalTime openTime = requestDto.getOpenTime();
        LocalTime closeTime = requestDto.getCloseTime();

        if (!openTime.isBefore(closeTime)) {
            throw new CustomException(ErrorCode.STORE_INVALID_TIME);
        }


        String foodCategory = requestDto.getCategory();
        FoodCategory category = Optional.ofNullable(FoodCategory.fromKr(foodCategory))
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Long userId = Long.valueOf(userDetails.getUsername());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (storeRepository.existsByUserAndNameAndIsDeletedFalse(user, requestDto.getName())) {
            throw new CustomException(ErrorCode.STORE_NAME_DUPLICATED);
        }

        // 유저 권한이 점주가 아니라면 권한 부여 및 토큰 재발급
        if (user.getRole() != UserRole.OWNER) {

            user.setRole(UserRole.OWNER);

            // 기존 토큰 Redis에서 삭제
            String redisKey = "refreshToken:" + user.getUserId();
            stringRedisTemplate.delete(redisKey);

            // 권한 반영된 토큰 생성
            TokenResponse newToken = jwtUtil.createToken
                    (
                            user.getUserId(),
                            user.getLoginId(),
                            user.getRole()
                    );

            // Refresh Token을 Redis에 저장
            redisKey = "refreshToken:" + user.getUserId();
            String refreshTokenField = "refreshToken";
            String refreshToken = newToken.getRefreshToken();
            long refreshTokenTime = jwtUtil.getRefreshTokenTime();

            String fullRefreshTokenFromGeneratedToken = newToken.getRefreshToken();
            String cleanRefreshTokenForRedis;

            // 'Bearer ' 접두사가 있는지 확인하고, 있다면 제거 후 양쪽 공백도 제거
            if (StringUtils.hasText(fullRefreshTokenFromGeneratedToken) && fullRefreshTokenFromGeneratedToken.startsWith(JwtUtil.BEARER_PREFIX)) {
                cleanRefreshTokenForRedis = fullRefreshTokenFromGeneratedToken.substring(JwtUtil.BEARER_PREFIX.length()).trim();
            } else {
                // 혹시라도 접두사 없이 토큰이 생성되었다면 (일반적이지 않음) 그냥 trim()만 적용
                cleanRefreshTokenForRedis = fullRefreshTokenFromGeneratedToken.trim();
            }

            // 새로운 Refresh Token 및 추가 정보 해시 테이블에 저장
            Map<String, String> hashData = new HashMap<>();
            hashData.put(refreshTokenField, cleanRefreshTokenForRedis);
            hashData.put("loginId", user.getLoginId()); // 로그인 ID도 함께 저장하여 활용 가능

            // 해시 테이블 형식으로 Redis에 저장
            stringRedisTemplate.opsForHash().putAll(redisKey, hashData);

            // 5.3 해시 테이블 전체에 만료 시간 설정
            stringRedisTemplate.expire(redisKey, refreshTokenTime, TimeUnit.MILLISECONDS);

            return newToken;
        }

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

        return null;
    }

    @Transactional(readOnly = true)
    public PagingStoreResponseDto getStoresByCategory(FoodCategory category, Pageable pageable) {

        String cacheKey = "storeList:" + category + ":page:" + pageable.getPageNumber();

        PagingStoreResponseDto cache = pagingStoreRedisTemplate.opsForValue().get(cacheKey);

        // 캐싱된 키가 존재할 시 즉시 캐시 데이터 반환
        if (cache != null) {
            return cache;
        }

        Pageable storesByCategory = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Store> getStoresByCategory = storeRepository.findByFoodCategoryAndIsDeletedFalse(category, storesByCategory);

        PagingStoreResponseDto responseDto = PagingStoreResponseDto.formPage(getStoresByCategory);
        pagingStoreRedisTemplate.opsForValue().set(cacheKey, responseDto);

        return responseDto;
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

        String cacheKey = "store:" + storeId;

        StoreResponseDto cache = storeRedisTemplate.opsForValue().get(cacheKey);

        // 캐싱된 키가 존재할 시 즉시 캐시 데이터 반환
        if (cache != null) {
            return cache;
        }


        Store store = storeRepository.findByStoreIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));


        StoreResponseDto responseDto = StoreResponseDto.from(store);
        storeRedisTemplate.opsForValue().set(cacheKey, responseDto);

        return responseDto;
    }

    @Transactional(readOnly = true)
    public PagingStoreResponseDto getStoreBySearch(String keyword, Pageable pageable) {

        Pageable bySearch = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Store> response = storeRepository.findBySearch(keyword, bySearch);

        if (response.isEmpty()) {
            throw new CustomException(ErrorCode.STORE_SEARCH_NO_RESULT);
        }

        return PagingStoreResponseDto.formPage(response);
    }

    @Transactional
    public StoreResponseDto updateStore(Long storeId, StoreUpdateRequestDto request, UserDetails userDetails) {

        Long userId = Long.valueOf(userDetails.getUsername());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByStoreIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (!user.getUserId().equals(store.getUser().getUserId())) {
            throw new CustomException(ErrorCode.STORE_ACCESS_DENIED);
        }

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

    @Transactional
    public void deleteStore(Long storeId, UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByStoreIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (!user.getUserId().equals(store.getUser().getUserId())) {
            throw new CustomException(ErrorCode.STORE_ACCESS_DENIED);
        }

        store.deleted();
    }

    @Transactional
    public void openStore(Long storeId) {

        Store store = storeRepository.findByStoreIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (store.isOpen()) {
            throw new CustomException(ErrorCode.STORE_ALREADY_OPEN);
        }

        store.openStore();
    }

    @Transactional
    public void closeStore(Long storeId) {

        Store store = storeRepository.findByStoreIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (!store.isOpen()) {
            throw new CustomException(ErrorCode.STORE_ALREADY_CLOSED);
        }

        store.closeStore();
    }
}
