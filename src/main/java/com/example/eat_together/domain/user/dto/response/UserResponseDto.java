package com.example.eat_together.domain.user.dto.response;

import com.example.eat_together.domain.user.entity.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class UserResponseDto {

    private final Long userId;
    private final String loginId;
    private final String email;
    private final String nickname;

    @Setter
    private UserRole role;

    private final LocalDateTime createdAt;
    private final LocalDateTime updateAt;

    public UserResponseDto(Long userId, String loginId, String email, String nickname) {
        this.userId = userId;
        this.loginId = loginId;
        this.email = email;
        this.nickname = nickname;
        this.role = UserRole.USER;
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
}
