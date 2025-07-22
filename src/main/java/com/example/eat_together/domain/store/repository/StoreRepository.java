package com.example.eat_together.domain.store.repository;

import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.entity.category.FoodCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Page<Store> findByFoodCategoryAndIsDeletedFalse(FoodCategory category, Pageable pageable);

}
