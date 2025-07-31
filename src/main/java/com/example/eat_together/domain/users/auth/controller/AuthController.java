package com.example.eat_together.domain.users.auth.controller;

import com.example.eat_together.domain.users.common.enums.MessageEnum;
import com.example.eat_together.global.dto.ApiResponse;
import com.example.eat_together.domain.users.common.dto.request.LoginRequestDto;
import com.example.eat_together.domain.users.common.dto.request.SignupRequestDto;
import com.example.eat_together.domain.users.common.dto.response.UserResponseDto;
import com.example.eat_together.domain.users.auth.service.AuthService;
import com.example.eat_together.global.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDto>> signup(@Valid @RequestBody SignupRequestDto request){

        UserResponseDto signup = authService.signup(request);

        return ResponseEntity.ok(ApiResponse.of(signup, MessageEnum.SIGNUP.getMessage()));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequestDto request){
        TokenResponse login = authService.login(request);

        return ResponseEntity.ok(ApiResponse.of(login,MessageEnum.LOGIN.getMessage()));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@AuthenticationPrincipal UserDetails userDetails) {

        authService.logout(Long.valueOf(userDetails.getUsername()));

        return ResponseEntity.ok(ApiResponse.success(MessageEnum.LOGOUT.getMessage()));
    }
}
