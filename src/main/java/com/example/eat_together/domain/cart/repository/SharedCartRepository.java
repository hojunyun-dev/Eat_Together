package com.example.eat_together.domain.cart.repository;

import com.example.eat_together.domain.cart.entity.SharedCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 공유 장바구니 관련 JPA 레포지토리
 */
public interface SharedCartRepository extends JpaRepository<SharedCart, Long> {

    /**
     * 채팅방 ID로 공유 장바구니 조회
     *
     * @param chatRoomId 채팅방 ID
     * @return 공유 장바구니 엔티티 Optional
     */
    Optional<SharedCart> findByChatRoomId(Long chatRoomId);
}