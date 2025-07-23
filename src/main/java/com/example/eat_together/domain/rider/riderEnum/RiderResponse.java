package com.example.eat_together.domain.rider.riderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RiderResponse {

    // Rider 생성
    RIDER_CREATED_SUCCESS("라이더가 성공적으로 등록되었습니다."),

    // Rider 단건 조회
    RIDER_FOUND_SUCCESS("라이더 정보를 성공적으로 조회했습니다."),
    RIDER_NOT_FOUND("해당 라이더를 찾을 수 없습니다."),

    // Rider 전체 조회
    RIDER_LIST_FOUND_SUCCESS("라이더 목록을 성공적으로 조회했습니다."),

    // Rider 수정
    RIDER_UPDATED_SUCCESS("라이더 정보가 성공적으로 수정되었습니다."),

    // Rider 삭제
    RIDER_DELETED_SUCCESS("라이더가 성공적으로 삭제되었습니다.");

    private final String message;
}

