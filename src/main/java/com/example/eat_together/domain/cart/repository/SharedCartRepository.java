package com.example.eat_together.domain.cart.repository;

import com.example.eat_together.domain.cart.entity.SharedCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SharedCartRepository extends JpaRepository<SharedCart, Long> {
    Optional<SharedCart> findByChatRoomId(Long chatRoomId);
}
