package com.example.eat_together.domain.user.controller;

import com.example.eat_together.global.dto.ApiResponse;
import com.example.eat_together.domain.user.dto.request.LoginRequestDto;
import com.example.eat_together.domain.user.dto.request.SignupRequestDto;
import com.example.eat_together.domain.user.dto.response.UserResponseDto;
import com.example.eat_together.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto request){

        UserResponseDto signup = authService.signup(request);

        return ApiResponse.of(signup,"회원가입 성공");
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody LoginRequestDto request){
        String login = authService.login(request);

        return ApiResponse.of(login,"로그인 성공");
    }
}
