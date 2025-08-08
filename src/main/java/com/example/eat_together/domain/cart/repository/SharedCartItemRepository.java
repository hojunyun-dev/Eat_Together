package com.example.eat_together.domain.cart.repository;

import com.example.eat_together.domain.cart.entity.SharedCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedCartItemRepository extends JpaRepository<SharedCartItem, Long> {

    List<SharedCartItem> findBySharedCart_ChatRoom_IdAndUser_UserId(Long roomId, Long userId);

    List<SharedCartItem> findBySharedCart_ChatRoom_Id(Long roomId);
}
