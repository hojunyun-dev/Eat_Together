package com.example.eat_together.domain.rider.service;

import com.example.eat_together.domain.rider.entity.Rider;
import com.example.eat_together.domain.rider.repository.RiderRepository;
import com.example.eat_together.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return riderRepository.findAllActiveRiders();
    }

    //라이더 단건 조회
    public Rider getRiderById(Long id) {
        return riderRepository.findActiveRiderById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 라이더를 찾을 수 없습니다."));
    }

    //라이더 정보 수정
    public Rider updateRider(Long id, String name, String phone) {
        Rider rider = riderRepository.findActiveRiderById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 라이더 정보가 존재하지 않습니다."));
        rider.update(name, phone);
        return rider;
    }
    //라이더 삭제
    @Transactional
    public void deleteRider(Long id) {
        Rider rider = riderRepository.findActiveRiderById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 라이더를 찾을 수 없습니다."));

        rider.delete();
    }
}
