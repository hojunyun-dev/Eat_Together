package com.example.eat_together.domain.rider.controller;

import com.example.eat_together.domain.rider.dto.request.RiderRequestDto;
import com.example.eat_together.domain.rider.entity.Rider;
import com.example.eat_together.domain.rider.service.RiderService;
import com.example.eat_together.domain.rider.riderEnum.RiderResponse;
import com.example.eat_together.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/riders")
public class RiderController {

    private final RiderService riderService;

    // 라이더 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Rider>> createRider(@RequestBody RiderRequestDto requestDto) {
        Rider rider = riderService.createRider(requestDto.getName(), requestDto.getPhone());
        return ResponseEntity.ok(ApiResponse.of(rider, RiderResponse.RIDER_CREATED_SUCCESS.getMessage()));
    }



    // 전체 라이더 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<Rider>>> getAllRiders() {
        List<Rider> riders = riderService.getAllRiders();
        return ResponseEntity.ok(ApiResponse.of(riders, RiderResponse.RIDER_LIST_FOUND_SUCCESS.getMessage()));
    }

    // 라이더 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Rider>> getRider(@PathVariable Long id) {
        Rider rider = riderService.getRiderById(id);
        return ResponseEntity.ok(ApiResponse.of(rider, RiderResponse.RIDER_FOUND_SUCCESS.getMessage()));
    }

    //라이더 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRider(@PathVariable Long id) {
        riderService.deleteRider(id);
        return ResponseEntity.ok(ApiResponse.of(null, "라이더가 성공적으로 삭제되었습니다."));
    }


    // 라이더 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Rider>> updateRider(
            @PathVariable Long id,
            @Valid @RequestBody RiderRequestDto requestDto
    ) {
        Rider updated = riderService.updateRider(id, requestDto.getName(), requestDto.getPhone());
        return ResponseEntity.ok(ApiResponse.of(updated, RiderResponse.RIDER_UPDATED_SUCCESS.getMessage()));
    }
}
