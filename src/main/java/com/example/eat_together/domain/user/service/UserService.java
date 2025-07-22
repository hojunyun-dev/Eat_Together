package com.example.eat_together.domain.user.service;

import com.example.eat_together.domain.user.dto.request.UpdateUserInfoRequestDto;
import com.example.eat_together.domain.user.dto.response.UserResponseDto;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.eat_together.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    // 개인 정보 수정
    @Transactional
    public UserResponseDto updateProfile(Long userId,
                                         UpdateUserInfoRequestDto request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호를 한번 더 확인 후 정보 변경을 할 것인가 ? 만약 한다면 여기다가 추가하면 됌

        user.updateProfile(request);
        User saveUser = userRepository.save(user);
        saveUser.setUpdatedAt(LocalDateTime.now());

        return new UserResponseDto(saveUser);
    }

    // 유저 단건 조회
    public UserResponseDto findUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserResponseDto(user);
    }

    // 유저 전체 조회
    public List<UserResponseDto> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDto::toDto)
                .toList();
    }
}
