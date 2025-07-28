package com.example.eat_together.domain.store.controller;

import com.example.eat_together.domain.store.dto.request.StoreRequestDto;
import com.example.eat_together.domain.store.dto.request.StoreUpdateRequestDto;
import com.example.eat_together.domain.store.dto.response.PagingStoreResponseDto;
import com.example.eat_together.domain.store.dto.response.StoreResponseDto;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.entity.category.FoodCategory;
import com.example.eat_together.domain.store.message.ResponseMessage;
import com.example.eat_together.domain.store.service.StoreService;
import com.example.eat_together.global.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<ApiResponse> createStore(@AuthenticationPrincipal UserDetails user,
                                                   @Valid @RequestBody StoreRequestDto requestDto) {

        storeService.createStore(user, requestDto);

        ApiResponse response = new ApiResponse<>
                (
                        ResponseMessage.STORE_CREATED_SUCCESS.getMessage(),
                        null
                );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagingStoreResponseDto>> getStoresByCategory(@RequestParam String category,
                                                                                   @PageableDefault Pageable pageable) {
        // 쿼리 파라미터로 받은 문자를 enum에 찾아서 매핑
        FoodCategory foodCategory = FoodCategory.fromKr(category);

        PagingStoreResponseDto storesByCategory = storeService.getStoresByCategory(foodCategory, pageable);

        ApiResponse<PagingStoreResponseDto> response = new ApiResponse<>
                (
                        ResponseMessage.STORE_LIST_FETCH_SUCCESS.getMessage(),
                        storesByCategory
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<PagingStoreResponseDto>> getStoresByUserId(@AuthenticationPrincipal UserDetails user,
                                                                                 @PageableDefault Pageable pageable) {

        PagingStoreResponseDto storesByUserId = storeService.getStoresByUserId(user, pageable);

        ApiResponse<PagingStoreResponseDto> response = new ApiResponse<>
                (
                        ResponseMessage.STORE_MY_LIST_FETCH_SUCCESS.getMessage(),
                        storesByUserId
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponseDto>> getStore(@PathVariable Long storeId) {

        StoreResponseDto store = storeService.getStore(storeId);

        ApiResponse<StoreResponseDto> response = new ApiResponse<>
                (
                        ResponseMessage.STORE_FETCH_SUCCESS.getMessage(),
                        store
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagingStoreResponseDto>> getStoreBySearch(@RequestParam String keyword,
                                                                                @PageableDefault Pageable pageable) {

        PagingStoreResponseDto storeBySearch = storeService.getStoreBySearch(keyword, pageable);

        ApiResponse<PagingStoreResponseDto> response = new ApiResponse<>
                (
                        ResponseMessage.STORE_SEARCH_SUCCESS.getMessage(),
                        storeBySearch
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{storeId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StoreResponseDto>> updateStore(@PathVariable Long storeId,
                                                                     @RequestBody StoreUpdateRequestDto request,
                                                                     @AuthenticationPrincipal UserDetails userDetails) {

        StoreResponseDto storeResponseDto = storeService.updateStore(storeId, request, userDetails);

        ApiResponse<StoreResponseDto> response = new ApiResponse<>
                (
                        ResponseMessage.STORE_UPDATED_SUCCESS.getMessage(),
                        storeResponseDto
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse> deleteStore(@PathVariable Long storeId,
                                                   @AuthenticationPrincipal UserDetails userDetails) {

        storeService.deleteStore(storeId, userDetails);

        ApiResponse<Store> response = new ApiResponse<>
                (
                        ResponseMessage.STORE_DELETED_SUCCESS.getMessage(),
                        null
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{storeId}/open")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse> openStore(@PathVariable Long storeId) {

        storeService.openStore(storeId);

        ApiResponse<Store> response = new ApiResponse<>
                (
                        ResponseMessage.STORE_OPEN_SUCCESS.getMessage(),
                        null
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{storeId}/close")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse> closeStore(@PathVariable Long storeId) {

        storeService.closeStore(storeId);

        ApiResponse<Store> response = new ApiResponse<>
                (
                        ResponseMessage.STORE_CLOSE_SUCCESS.getMessage(),
                        null
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
