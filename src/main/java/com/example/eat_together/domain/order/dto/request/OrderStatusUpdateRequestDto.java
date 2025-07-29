package com.example.eat_together.domain.order.dto.request;

import com.example.eat_together.domain.order.orderEnum.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderStatusUpdateRequestDto {

    @NotNull(message = "주문 상태를 입력해주세요.")
    private OrderStatus status;

}
