package com.example.eat_together.domain.notification.service;

import com.example.eat_together.domain.notification.dto.NotificationResponseDto;
import com.example.eat_together.domain.notification.entity.Notification;
import com.example.eat_together.domain.notification.enums.NotificationType;
import com.example.eat_together.domain.notification.messaging.NotificationProducerService;
import com.example.eat_together.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 알림 저장/조회/읽음/삭제 + 카프카 발행까지 여기서 끝낸다.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repo;
    private final NotificationProducerService producer;

    //해당 메서드는 알림을 DB에 저장하고 동시에 실시간 전송도 하기 위해서 만든 매서드입니다.
    @Transactional
    public void createAndPublish(Long userId, NotificationType type, String title, String message, String link) {
        Notification saved = repo.save(Notification.create(userId, type, title, message, link));
        producer.send(userId, NotificationResponseDto.of(saved));
    }

    //내 알림 목록(페이징 적용)
    @Transactional(readOnly = true)
    public Page<NotificationResponseDto> getMyNotifications(Long userId, Pageable pageable) {
        return repo.findByUserIdAndIsDeletedFalseOrderByIdDesc(userId, pageable)
                .map(NotificationResponseDto::of);
    }

    //단건 읽음
    @Transactional
    public void markRead(Long userId, Long id) { repo.markRead(userId, id); }

    //전체 읽음
    @Transactional
    public void markAllRead(Long userId) { repo.markAllRead(userId); }

    //알림 삭제
    @Transactional
    public void delete(Long userId, Long id) { repo.softDelete(userId, id); }
}
