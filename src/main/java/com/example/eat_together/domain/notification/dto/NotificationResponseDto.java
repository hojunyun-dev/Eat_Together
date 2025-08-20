package com.example.eat_together.domain.notification.dto;

import com.example.eat_together.domain.notification.entity.Notification;
import com.example.eat_together.domain.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

//클라우드에 내려줄 알림 응답 DTO
@Getter
@Builder
public class NotificationResponseDto {
    private Long id;
    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private String link;
    private boolean isRead;
    private LocalDateTime createdAt;

    // 엔티티 -> DTO 변환
    public static NotificationResponseDto of(Notification n) {
        return NotificationResponseDto.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .link(n.getLink())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
