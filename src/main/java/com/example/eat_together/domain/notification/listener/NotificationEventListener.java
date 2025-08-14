package com.example.eat_together.domain.notification.listener;

import com.example.eat_together.domain.notification.enums.NotificationType;
import com.example.eat_together.domain.notification.event.OrderStatusChangedEvent;
import com.example.eat_together.domain.notification.event.RiderStatusChangedEvent;
import com.example.eat_together.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * "한 줄 이벤트 발행"을 여기서 받아서 -> 저장 + 카프카 발행까지 이어준다.
 * 굳이 @Async 안 쓴 이유: 프로젝트에 @EnableAsync 없는 상황 고려. 필요하면 쉽게 추가 가능.
 */
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final NotificationService notificationService;

    // 주문 상태 바뀜 -> 주문자에게 알림
    @EventListener
    public void onOrderStatusChanged(OrderStatusChangedEvent e) {
        notificationService.createAndPublish(
                e.userId(), NotificationType.ORDER,
                "주문 상태 변경", "주문 #" + e.orderId() + " : " + e.from() + " → " + e.to(),
                "/orders/" + e.orderId()
        );
    }

    // 라이더 상태 바뀜 -> 관련 유저(전달 인자로 받은 userId)에게 알림
    @EventListener
    public void onRiderStatusChanged(RiderStatusChangedEvent e) {
        notificationService.createAndPublish(
                e.userId(), NotificationType.RIDER,
                "라이더 상태 변경", "라이더 #" + e.riderId() + " : " + e.from() + " → " + e.to(),
                "/riders/" + e.riderId()
        );
    }
}

