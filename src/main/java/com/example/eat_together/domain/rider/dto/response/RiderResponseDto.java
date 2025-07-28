package com.example.eat_together.domain.rider.dto.response;

import com.example.eat_together.domain.rider.entity.Rider;
import com.example.eat_together.domain.rider.riderEnum.RiderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class RiderResponseDto {

    private Long id;
    private String name;
    private String phone;
    private RiderStatus status;//추가

    private Long userId;
    private String userName;
    private String userLoginId;
    private LocalTime openTime;
    private LocalTime closeTime;

    /*private String vehicleType;*/ //추후에 사용할지 결정

    public static RiderResponseDto of(Rider rider) {
        return RiderResponseDto.builder()
                .id(rider.getId())
                .name(rider.getName())
                .phone(rider.getPhone())
                .status(rider.getStatus())
                .userId(rider.getUser().getUserId())
                .userName(rider.getUser().getName())
                .userLoginId(rider.getUser().getLoginId())
                .openTime(rider.getOpenTime()) //추가
                .closeTime(rider.getCloseTime())//추가
                .build();
    }
}
