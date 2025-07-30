package com.example.eat_together.domain.store.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class StoreRequestDto {

    @NotBlank(message = "가게 이름이 없습니다.")
    @Size(max = 50, message = "가게 이름은 50자 이하여야 합니다.")
    private String name;

    @NotBlank(message = "가게 설명이 없습니다.")
    @Size(max = 255, message = "가게 소개는 255자 이하여야 합니다.")
    private String description;

    @NotBlank(message = "가게 주소가 없습니다.")
    @Size(max = 255, message = "가게 주소는 255자 이하여야 합니다.")
    private String address;

    @NotNull(message = "가게 오픈 시간이 없습니다.")
    private LocalTime openTime;

    @NotNull(message = "가게 영업 종료 시간이 없습니다.")
    private LocalTime closeTime;

    @NotNull(message = "배달비가 없습니다.")
    @Min(value = 0, message = "배달비는 음수가 될 수 없습니다.")
    private int deliveryFee;

    @NotBlank(message = "가게 카테고리가 없습니다.")
    private String category;

    @NotBlank(message = "가게 번호가 없습니다.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phoneNumber;


    public StoreRequestDto(String name,
                           String description,
                           String address,
                           LocalTime openTime,
                           LocalTime closeTime,
                           int deliveryFee,
                           String category,
                           String phoneNumber
    ) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.deliveryFee = deliveryFee;
        this.category = category;
        this.phoneNumber = phoneNumber;
    }
}
