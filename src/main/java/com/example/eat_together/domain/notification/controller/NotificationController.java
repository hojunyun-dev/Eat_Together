package com.example.eat_together.domain.notification.controller;

import com.example.eat_together.domain.notification.dto.NotificationResponseDto;
import com.example.eat_together.domain.notification.service.NotificationService;
import com.example.eat_together.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    //내 알림 목록 조회
    //해당 메서드는 알림함을 페이징으로 보여주도록 구현했습니다.
    @GetMapping
    public ApiResponse<Page<NotificationResponseDto>> list(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long userId = Long.valueOf(principal.getUsername());
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return ApiResponse.of(notificationService.getMyNotifications(userId, pageable), "알림 목록 조회");
    }

    //단건 읽음
    @PatchMapping("/{id}/read")
    public ApiResponse<Void> read(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
        Long userId = Long.valueOf(principal.getUsername());
        notificationService.markRead(userId, id);
        return ApiResponse.of(null, "읽음 처리 완료");
    }

    //전체 읽음
    @PatchMapping("/read-all")
    public ApiResponse<Void> readAll(@AuthenticationPrincipal UserDetails principal) {
        Long userId = Long.valueOf(principal.getUsername());
        notificationService.markAllRead(userId);
        return ApiResponse.of(null, "전체 읽음 처리 완료");
    }

    //알림 삭제
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
        Long userId = Long.valueOf(principal.getUsername());
        notificationService.delete(userId, id);
        return ApiResponse.of(null, "삭제 완료");
    }
}
