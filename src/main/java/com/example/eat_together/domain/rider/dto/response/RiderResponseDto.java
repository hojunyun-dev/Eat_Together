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
    private String vehicleType;

    public static RiderResponseDto of(Rider rider) {
        return RiderResponseDto.builder()
                .id(rider.getId())
                .name(rider.getName())
                .phone(rider.getPhone())
                .build();
    }
}
