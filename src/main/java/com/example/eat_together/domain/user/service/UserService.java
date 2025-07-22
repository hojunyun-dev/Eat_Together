package com.example.eat_together.domain.user.service;

import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.eat_together.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 비밀번호 변경
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDto request) {

        // 유저 조회하기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 현재 비밀번호와 바꿀 비밀번호 비교
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.PASSWORD_WRONG);
        }

        // 비밀번호 다시 인코딩 후 비밀번호 변경
        String encodePassword = passwordEncoder.encode(request.getNewPassword());

        user.updatePassword(encodePassword);
    }
}
