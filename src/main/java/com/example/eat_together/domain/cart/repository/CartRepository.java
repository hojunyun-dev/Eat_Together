package com.example.eat_together.domain.cart.repository;

import com.example.eat_together.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Cart 엔티티에 대한 JPA 리포지토리 인터페이스
 */
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * 사용자 ID로 장바구니 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 장바구니 (Optional)
     */
    Optional<Cart> findByUserUserId(Long userId);

    /**
     * 사용자 ID 기준 장바구니 존재 여부 확인
     *
     * @param userId 사용자 ID
     * @return 존재 여부 (true/false)
     */
    boolean existsByUserUserId(Long userId);
}
