package com.example.eat_together.user.controller;

import com.example.eat_together.global.dto.ApiResponse;
import com.example.eat_together.user.dto.request.ChangePasswordRequestDto;
import com.example.eat_together.user.dto.response.UserResponseDto;
import com.example.eat_together.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PatchMapping("/{userId}/password")
    public ApiResponse<Void> changePassword(@PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequestDto request){

        userService.changePassword(userId,request);

        return null;
    }
}
