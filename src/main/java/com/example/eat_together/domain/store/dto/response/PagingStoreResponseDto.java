package com.example.eat_together.domain.store.dto.response;

import com.example.eat_together.domain.store.entity.Store;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PagingStoreResponseDto {

    List<StoreResponseDto> storeList;

    public static PagingStoreResponseDto formPage(Page<Store> page) {
        List<StoreResponseDto> pagingStore =
                page
                        .getContent()
                        .stream()
                        .map(Store -> StoreResponseDto
                                .builder()
                                .storeId(Store.getStoreId())
                                .name(Store.getName())
                                .description(Store.getDescription())
                                .address(Store.getAddress())
                                .isOpen(Store.isOpen())
                                .openTime(Store.getOpenTime())
                                .closeTime(Store.getCloseTime())
                                .category(Store.getFoodCategory())
                                .phoneNumber(Store.getPhoneNumber())
                                .createdAt(Store.getCreatedAt())
                                .updatedAt(Store.getUpdatedAt())
                                .build())
                        .toList();

        return PagingStoreResponseDto
                .builder()
                .storeList(pagingStore)
                .build();
    }

}
