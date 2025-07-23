package com.example.eat_together.domain.user.dto.response;

import com.example.eat_together.domain.user.entity.User;
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
    private final String name;

    @Setter
    private UserRole role;

    private final LocalDateTime createdAt;

    @Setter
    private LocalDateTime updateAt;

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.loginId = user.getLoginId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.role = UserRole.USER;
        this.createdAt = user.getCreatedAt();
        this.updateAt = user.getUpdatedAt();
    }

    public static UserResponseDto toDto(User user){
        return new UserResponseDto(user);
    }
}
