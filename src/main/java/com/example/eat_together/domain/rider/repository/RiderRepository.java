package com.example.eat_together.domain.rider.repository;

import com.example.eat_together.domain.rider.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, Long> {

    //라이더 전체 조회
    List<Rider> findAllByIsDeletedFalse();

    //라이더 단건 조회
    Optional<Rider> findByIdAndIsDeletedFalse(Long id);
}
