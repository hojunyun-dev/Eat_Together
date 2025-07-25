package com.example.eat_together.domain.rider.riderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RiderStatus {
    AVAILABLE("배달 가능"),
    UNAVAILABLE("배달 불가");

    private final String description;
}
