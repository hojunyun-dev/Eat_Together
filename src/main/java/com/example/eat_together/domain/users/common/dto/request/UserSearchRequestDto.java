package com.example.eat_together.domain.users.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserSearchRequestDto {
    private final String name;
    private final String nickname;
}
