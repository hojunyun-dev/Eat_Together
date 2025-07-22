package com.example.eat_together.domain.user.controller;

import com.example.eat_together.domain.user.dto.request.UpdateUserInfoRequestDto;
import com.example.eat_together.domain.user.dto.response.UserResponseDto;
import com.example.eat_together.global.dto.ApiResponse;
import com.example.eat_together.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.eat_together.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 비밀번호 변경
    @PatchMapping("/password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                            @Valid @RequestBody ChangePasswordRequestDto request){
        userService.changePassword(Long.valueOf(userDetails.getUsername()),request);
        return ApiResponse.success("비밀번호 변경 성공");
    }

    // 개인 정보 수정 ( email, name, nickname )
    @PatchMapping("/update/profile")
    public ApiResponse<UserResponseDto> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                      @Valid @RequestBody UpdateUserInfoRequestDto request){
        log.info(String.valueOf(userDetails));
        UserResponseDto userResponseDto = userService.updateProfile(Long.valueOf(userDetails.getUsername()),request);

        return ApiResponse.of(userResponseDto,"정보 수정 완료");
    }

    // 유저 단건 조회 ( ADMIN 전용 )
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponseDto> findUser(@PathVariable Long userId){

        UserResponseDto user = userService.findUser(userId);

        return ApiResponse.of(user,"유저 조회 완료");
    }

    // 유저 전체 조회 ( ADMIN 전용 )
    @GetMapping("/find/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserResponseDto>> findAllUsers(){

        List<UserResponseDto> allUsers = userService.findAllUsers();

        return ApiResponse.of(allUsers,"전체 유저 조회 완료");
    }


    // TODO : 마이페이지 조회,유저 삭제
}
