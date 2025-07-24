package com.example.eat_together.domain.rider.repository;

import com.example.eat_together.domain.rider.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, Long> {

    //라이더 전체 조회
    @Query("SELECT r FROM Rider r WHERE r.isDeleted = false")
    List<Rider> findAllActiveRiders();

    //라이더 단건 조회
    @Query("SELECT r FROM Rider r WHERE r.id = :id AND r.isDeleted = false")
    Optional<Rider> findActiveRiderById(@Param("id") Long id);
}
