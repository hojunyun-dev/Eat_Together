package com.example.eat_together.domain.user.dto.response;

import com.example.eat_together.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserInfoResponseDto {
    private String name;
    private String nickname;

    // User 엔티티로부터 UserInfoResponseDto를 생성하는 생성자
    public UserInfoResponseDto(User user) {
        this.name = user.getName();
        this.nickname = user.getNickname();
    }

    public static List<UserInfoResponseDto> todo(List<User> users) {
        return users.stream()
                .map(UserInfoResponseDto::new)
                .collect(Collectors.toList());
    }
}
