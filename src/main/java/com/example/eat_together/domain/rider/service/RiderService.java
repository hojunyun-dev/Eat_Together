package com.example.eat_together.domain.rider.service;

import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.order.orderEnum.OrderStatus;
import com.example.eat_together.domain.order.repository.OrderRepository;
import com.example.eat_together.domain.rider.dto.request.RiderRequestDto;
import com.example.eat_together.domain.rider.entity.Rider;
import com.example.eat_together.domain.rider.repository.RiderRepository;
import com.example.eat_together.domain.rider.riderEnum.RiderStatus;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    // 라이더 등록
    public Rider createRider(String userIdStr, RiderRequestDto requestDto) {
        Long userId = Long.parseLong(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Rider rider = Rider.of(
                user,
                requestDto.getPhone(),
                requestDto.getOpenTime(),
                requestDto.getCloseTime()
        );

        return riderRepository.save(rider);
    }

    // 전체 라이더 목록 조회
    public List<Rider> getAllRiders() {
        return riderRepository.findAllActiveRiders();
    }

    // 라이더 단건 조회
    public Rider getRiderById(Long id) {
        return riderRepository.findActiveRiderById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 라이더를 찾을 수 없습니다."));
    }

    // 라이더 정보 수정
    public Rider updateRider(Long id, RiderRequestDto requestDto) {
        Rider rider = riderRepository.findActiveRiderById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 라이더 정보가 존재하지 않습니다."));

        rider.update(
                requestDto.getPhone(),
                requestDto.getOpenTime(),
                requestDto.getCloseTime()
        );

        return rider;
    }

    // 라이더 삭제
    @Transactional
    public void deleteRider(Long id) {
        Rider rider = riderRepository.findActiveRiderById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 라이더를 찾을 수 없습니다."));

        rider.delete();
    }

    // 라이더 상태 변경
    @Transactional
    public void changeStatus(Long id, RiderStatus status) {
        Rider rider = getRiderById(id);
        rider.changeStatus(status);

        /**
         * 라이더가 상태 변경 시 주문 상태 동기화
         * -RiderStatus.UNAVAILABLE: 라이더가 배달 시작 → 해당 라이더의 모든 ORDERED 주문을 DELIVERING으로 변경
         * -RiderStatus.AVAILABLE: 라이더가 다시 배달 가능 → 해당 라이더의 모든 DELIVERING 주문을 DELIVERED로 변경
         */

        if (status == RiderStatus.UNAVAILABLE) {
            List<Order> orders = orderRepository.findByRider_IdAndStatus(rider.getId(), OrderStatus.ORDERED);
            orders.forEach(order -> order.updateStatus(OrderStatus.DELIVERING));

        } else if (status == RiderStatus.AVAILABLE) {
            List<Order> orders = orderRepository.findByRider_IdAndStatus(rider.getId(), OrderStatus.DELIVERING);
            orders.forEach(order -> order.updateStatus(OrderStatus.DELIVERED));
        }
    }
}
