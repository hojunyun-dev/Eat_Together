package com.example.eat_together.domain.rider.service;

import com.example.eat_together.domain.rider.entity.Rider;
import com.example.eat_together.domain.rider.repository.RiderRepository;
import com.example.eat_together.domain.rider.riderEnum.RiderStatus;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.eat_together.domain.user.entity.User;//추가


import java.util.List;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;
    private final UserRepository userRepository;

    //라이더 등록
    public Rider createRider(String userIdStr, String phone) {
        Long userId = Long.parseLong(userIdStr);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Rider rider = Rider.of(user, phone);
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
    public Rider updateRider(Long id, String phone) {
        Rider rider = riderRepository.findActiveRiderById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 라이더 정보가 존재하지 않습니다."));
        rider.update(phone);
        return rider;
    }
    //라이더 삭제
    @Transactional
    public void deleteRider(Long id) {
        Rider rider = riderRepository.findActiveRiderById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 라이더를 찾을 수 없습니다."));

        rider.delete();
    }

    // 라이더 배차 가능 여부  --> ENUM기반으로 수정
    @Transactional
    public void changeStatus(Long id, RiderStatus status) {
        Rider rider = getRiderById(id);
        rider.changeStatus(status);
    }

}
