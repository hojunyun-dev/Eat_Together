package com.example.eat_together.domain.rider.service;

import com.example.eat_together.domain.rider.dto.request.RiderRequestDto;
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

    //라이더 등록  ->   // RiderRequestDto에서 openTime, closeTime도 받아오도록 수정
    public Rider createRider(String userIdStr, RiderRequestDto requestDto) {
        Long userId = Long.parseLong(userIdStr);//
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Rider rider = Rider.of(  //Rider.of() 호출 시 openTime과 closeTime까지 전달
                user,
                requestDto.getPhone(),
                requestDto.getOpenTime(),
                requestDto.getCloseTime()
        );

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

    //라이더 정보 수정 ->영업 시작 및 종료시간도 포함하도록 수정
    public Rider updateRider(Long id, RiderRequestDto requestDto) {
        Rider rider = riderRepository.findActiveRiderById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 라이더 정보가 존재하지 않습니다."));

        //rider.update()호출 시 시간까지 포함
        rider.update(
                requestDto.getPhone(),
                requestDto.getOpenTime(),//추가된 시간 필드
                requestDto.getCloseTime() //추가된 시간 필드
        );

        return rider;
    }
    //라이더 삭제
    @Transactional
    public void deleteRider(Long id) {
        Rider rider = riderRepository.findActiveRiderById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 라이더를 찾을 수 없습니다."));

        rider.delete();
    }

    // 라이더 배차 가능 여부
    @Transactional
    public void changeStatus(Long id, RiderStatus status) {
        Rider rider = getRiderById(id);
        rider.changeStatus(status);
    }

}
