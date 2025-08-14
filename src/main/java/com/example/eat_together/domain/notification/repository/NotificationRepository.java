package com.example.eat_together.domain.notification.repository;

import com.example.eat_together.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    //내 알림 목록
    Page<Notification> findByUserIdAndIsDeletedFalseOrderByIdDesc(Long userId, Pageable pageable);

    //단건 읽음
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notification n set n.isRead = true where n.id = :id and n.userId = :userId and n.isDeleted = false")
    int markRead(@Param("userId") Long userId, @Param("id") Long id);

    //전체 읽음
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notification n set n.isRead = true where n.userId = :userId and n.isDeleted = false and n.isRead = false")
    int markAllRead(@Param("userId") Long userId);

    //알림 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notification n set n.isDeleted = true where n.id = :id and n.userId = :userId and n.isDeleted = false")
    int softDelete(@Param("userId") Long userId, @Param("id") Long id);
}
