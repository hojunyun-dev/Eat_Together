package com.example.eat_together.user.dto.response;

import com.example.eat_together.user.entity.UserRole;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponseDto {

    private final Long userId;
    private final String loginId;
    private final String email;
    private final String nickname;
    private final UserRole role;
    private final LocalDateTime createdAt;
    private final LocalDateTime updateAt;

    public UserResponseDto(Long userId, String loginId, String email, String nickname, UserRole role) {
        this.userId = userId;
        this.loginId = loginId;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
}
