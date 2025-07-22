package com.example.eat_together.domain.store.controller;

import com.example.eat_together.domain.store.dto.request.CreateStoreRequestDto;
import com.example.eat_together.domain.store.dto.response.PagingStoreResponseDto;
import com.example.eat_together.domain.store.entity.category.FoodCategory;
import com.example.eat_together.domain.store.service.StoreService;
import com.example.eat_together.global.dto.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createStore(@AuthenticationPrincipal UserDetails user, @RequestBody CreateStoreRequestDto requestDto) {

        storeService.createStore(user, requestDto);

        ApiResponse response = new ApiResponse<>("매장 등록이 완료되었습니다.", null);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagingStoreResponseDto>> getStoresByCategory(@RequestParam String category,
                                                                                   @PageableDefault Pageable pageable) {
        // 쿼리 파라미터로 받은 문자를 enum에 찾아서 매핑
        FoodCategory foodCategory = FoodCategory.fromKr(category);

        PagingStoreResponseDto storesByCategory = storeService.getStoresByCategory(foodCategory, pageable);

        ApiResponse<PagingStoreResponseDto> response = new ApiResponse<>("매장 목록 조회가 완료되었습니다.", storesByCategory);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
