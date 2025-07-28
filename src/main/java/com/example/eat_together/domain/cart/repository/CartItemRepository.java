package com.example.eat_together.domain.cart.repository;

import com.example.eat_together.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CartItem 엔티티에 대한 JPA 리포지토리 인터페이스
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
