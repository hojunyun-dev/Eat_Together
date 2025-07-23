package com.example.eat_together.domain.cart.repository;

import com.example.eat_together.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
