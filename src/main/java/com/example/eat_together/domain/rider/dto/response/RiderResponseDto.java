package com.example.eat_together.domain.rider.dto.response;

import com.example.eat_together.domain.rider.entity.Rider;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RiderResponseDto {

    private Long id;
    private String name;
    private String phone;
    /*private String vehicleType;*/ //추후에 사용할지 결정

    public static RiderResponseDto of(Rider rider) {
        return RiderResponseDto.builder()
                .id(rider.getId())
                .name(rider.getUser().getName()) //User에서 이름을 가져오도록
                .phone(rider.getPhone())
                .build();
    }
}
