package com.example.eat_together.domain.user.controller;

import com.example.eat_together.domain.user.dto.request.UpdateUserInfoRequestDto;
import com.example.eat_together.domain.user.dto.response.UserResponseDto;
import com.example.eat_together.global.dto.ApiResponse;
import com.example.eat_together.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.eat_together.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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


}
