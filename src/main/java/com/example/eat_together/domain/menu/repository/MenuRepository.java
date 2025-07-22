package com.example.eat_together.domain.menu.repository;

import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Page<Menu> findAllByStore(Store store, Pageable pageable);

    Menu findByMenuIdAndStore(Long menuId, Store store);

}
