package com.example.eat_together.domain.rider.controller;

import com.example.eat_together.domain.rider.dto.request.RiderRequestDto;
import com.example.eat_together.domain.rider.dto.request.RiderStatusRequestDto;
import com.example.eat_together.domain.rider.dto.response.RiderResponseDto;
import com.example.eat_together.domain.rider.entity.Rider;
import com.example.eat_together.domain.rider.service.RiderService;
import com.example.eat_together.domain.rider.riderEnum.RiderResponse;
import com.example.eat_together.global.dto.ApiResponse;
import com.example.eat_together.global.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eat_together.domain.user.entity.User;//추가
import org.springframework.security.core.annotation.AuthenticationPrincipal;//추가


import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/riders")
public class RiderController {

    private final RiderService riderService;

    //라이더 등록 (기존: phone만 전달 → requestDto 전체 전달)
    @PostMapping
    public ResponseEntity<ApiResponse<RiderResponseDto>> createRider(
            Principal principal,
            @Valid @RequestBody RiderRequestDto requestDto
    ) {
        Rider rider = riderService.createRider(principal.getName(), requestDto); //수정된 서비스에 맞춤
        return ResponseEntity.ok(
                ApiResponse.of(RiderResponseDto.of(rider), RiderResponse.RIDER_CREATED_SUCCESS.getMessage())
        );
    }


    // 전체 라이더 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<RiderResponseDto>>> getAllRiders() {
        List<Rider> riders = riderService.getAllRiders();
        List<RiderResponseDto> response = riders.stream()
                .map(RiderResponseDto::of)
                .toList();
        return ResponseEntity.ok(ApiResponse.of(response, RiderResponse.RIDER_LIST_FOUND_SUCCESS.getMessage()));
    }


    // 라이더 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RiderResponseDto>> getRider(@PathVariable Long id) {
        Rider rider = riderService.getRiderById(id);
        return ResponseEntity.ok(ApiResponse.of(RiderResponseDto.of(rider), RiderResponse.RIDER_FOUND_SUCCESS.getMessage()));
    }

    //라이더 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRider(@PathVariable Long id) {
        riderService.deleteRider(id);
        return ResponseEntity.ok(ApiResponse.success(RiderResponse.RIDER_DELETED_SUCCESS.getMessage()));
    }

    //라이더 정보 수정 (기존: phone만 넘김 → 전체 DTO 전달)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RiderResponseDto>> updateRider(
            @PathVariable Long id,
            @Valid @RequestBody RiderRequestDto requestDto
    ) {
        Rider updated = riderService.updateRider(id, requestDto); //수정된 서비스레 맞춤
        return ResponseEntity.ok(ApiResponse.of(RiderResponseDto.of(updated), RiderResponse.RIDER_UPDATED_SUCCESS.getMessage()));
    }


    //라이더 상태 변경( 예: 배달 가능/ 배달 불가)
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody RiderStatusRequestDto requestDto
    ) {
        riderService.changeStatus(id, requestDto.getStatus());
        return ResponseEntity.ok(ApiResponse.success(RiderResponse.RIDER_STATUS_UPDATED.getMessage()));

    }

}
