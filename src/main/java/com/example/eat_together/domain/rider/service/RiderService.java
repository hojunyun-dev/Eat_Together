package com.example.eat_together.domain.rider.service;

import com.example.eat_together.domain.rider.entity.Rider;
import com.example.eat_together.domain.rider.repository.RiderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;

    //라이더 등록
    public Rider createRider(String name, String phone) {
        Rider rider = Rider.of(name, phone);
        return riderRepository.save(rider);
    }

    //전체 라이더 목록 조회
    public List<Rider> getAllRiders() {
        return riderRepository.findAllByIsDeletedFalse();
    }

    //라이더 조회
    public Rider getRiderById(Long id) {
        return riderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 라이더를 찾을 수 없습니다."));
    }
}
