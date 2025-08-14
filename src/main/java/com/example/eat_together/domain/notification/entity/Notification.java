package com.example.eat_together.domain.notification.entity;

import com.example.eat_together.domain.notification.enums.NotificationType;
import com.example.eat_together.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

//목록 API/읽음처리/삭제 전부 이 테이블로 관리
//Soft Delete 적용
@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 수신자 사용자 ID (FK로 안 묶어도 숫자만으로 충분)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(length = 200)
    private String link;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    //생성 정적 팩토리
    public static Notification create(Long userId, NotificationType type, String title, String message, String link) {
        Notification n = new Notification();
        n.userId = userId;
        n.type = type;
        n.title = title;
        n.message = message;
        n.link = link;
        n.isRead = false;
        n.isDeleted = false;
        return n;
    }

    //"읽음" 체크
    public void markRead() { this.isRead = true; }

    //소프트 딜리트
    public void softDelete() { this.isDeleted = true; }
}
