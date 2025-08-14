package com.example.eat_together.domain.cart.repository;

import com.example.eat_together.domain.cart.entity.SharedCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 공유 장바구니 항목 관련 JPA 레포지토리
 */
public interface SharedCartItemRepository extends JpaRepository<SharedCartItem, Long> {

    /**
     * 채팅방 ID와 사용자 ID로 공유 장바구니 항목 목록 조회
     *
     * @param roomId 채팅방 ID
     * @param userId 사용자 ID
     * @return 공유 장바구니 항목 목록
     */
    List<SharedCartItem> findBySharedCart_ChatRoom_IdAndUser_UserId(Long roomId, Long userId);

    /**
     * 채팅방 ID로 공유 장바구니 항목 목록 조회
     *
     * @param roomId 채팅방 ID
     * @return 공유 장바구니 항목 목록
     */
    List<SharedCartItem> findBySharedCart_ChatRoom_Id(Long roomId);
}