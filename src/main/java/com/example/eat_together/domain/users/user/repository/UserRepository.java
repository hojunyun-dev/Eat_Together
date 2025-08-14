package com.example.eat_together.domain.users.user.repository;

import com.example.eat_together.domain.users.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByEmail(String email);
    List<User> findByName(String username);

    List<User> findByNickname(String nickname);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    // name 또는 nickname으로 사용자를 검색하는 통합 쿼리
    @Query("SELECT u FROM User u WHERE " +
            "(u.name = :name) OR " +
            "(u.nickname = :nickname)")
    List<User> findByNameOrNickname(@Param("name") String name, @Param("nickname") String nickname);

    boolean existsByName(String name);
}
