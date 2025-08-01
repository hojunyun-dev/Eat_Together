package com.example.eat_together.domain.users.common.dto.response;

import com.example.eat_together.domain.social.helper.SocialLoginType;
import com.example.eat_together.domain.users.common.entity.User;
import com.example.eat_together.domain.users.common.enums.UserRole;
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
    private final SocialLoginType socialLoginType;
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
        this.socialLoginType = user.getSocialLoginType();
    }

    public static UserResponseDto toDto(User user){
        return new UserResponseDto(user);
    }
}
