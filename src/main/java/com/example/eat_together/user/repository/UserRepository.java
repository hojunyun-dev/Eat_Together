package com.example.eat_together.user.repository;

import com.example.eat_together.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
