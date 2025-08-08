package com.example.eat_together.domain.store.repository;

import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.entity.category.FoodCategory;
import com.example.eat_together.domain.users.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Page<Store> findByFoodCategoryAndIsDeletedFalse(FoodCategory category, Pageable pageable);

    Page<Store> findStoresByUserAndIsDeletedFalse(User user, Pageable pageable);

    boolean existsByUserAndNameAndIsDeletedFalse(User user, String name);

    Page<Store> findByStoreIdInAndIsDeletedFalse(List<Long> storeIds, Pageable pageable);

    Optional<Store> findByStoreIdAndIsDeletedFalse(Long storeId);
}
