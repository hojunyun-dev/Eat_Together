package com.example.eat_together.domain.store.repository;

import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.entity.category.FoodCategory;
import com.example.eat_together.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Page<Store> findByFoodCategoryAndIsDeletedFalse(FoodCategory category, Pageable pageable);

    Page<Store> findStoresByUserAndIsDeletedFalse(User user, Pageable pageable);

    boolean existsByUserAndNameAndIsDeletedFalse(User user, String name);

    @Query("SELECT s FROM Store s WHERE s.isDeleted = false AND LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword,  '%')) ORDER BY s.createdAt DESC ")
    Page<Store> findBySearch(String keyword, Pageable pageable);

    Optional<Store> findByStoreIdAndIsDeletedFalse(Long storeId);
}
