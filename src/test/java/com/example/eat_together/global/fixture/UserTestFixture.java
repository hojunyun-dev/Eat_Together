package com.example.eat_together.global.fixture;

import com.example.eat_together.domain.users.common.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

public class UserTestFixture {

    // 추후 유저 생성 단계에서 권한 위임시 메서드 추가 가능
    // 현재는 일반 유저 생성 메서드만 존재

    public static User 유저_생성(Long userId) {
        User user = new User(
                "testLogin",
                "testName",
                "1q2w3e4r!",
                "test@email.com",
                "testNickname"
        );
        ReflectionTestUtils.setField(user, "userId", userId);

        return user;
    }
}
