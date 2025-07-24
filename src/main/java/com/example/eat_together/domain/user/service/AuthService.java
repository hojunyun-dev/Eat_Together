package com.example.eat_together.domain.user.service;

import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.global.dto.TokenResponse;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.util.JwtUtil;
import com.example.eat_together.domain.user.dto.request.LoginRequestDto;
import com.example.eat_together.domain.user.dto.request.SignupRequestDto;
import com.example.eat_together.domain.user.dto.response.UserResponseDto;
import com.example.eat_together.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    public UserResponseDto signup(SignupRequestDto request) {

        // 중복된 아이디 검증
        if(userRepository.existsByLoginId(request.getLoginId())){
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }

        String encodePassword = passwordEncoder.encode(request.getPassword());

        User user = new User(request, encodePassword);
        User saveUser = userRepository.save(user);

        return new UserResponseDto(saveUser);
    }

    // 로그인
    @Transactional
    public TokenResponse login(LoginRequestDto request) {

        // 유저 검증
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.INFO_MISMATCH));

        // 소프트 삭제 검증
        if(user.isDeleted()){
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.INFO_MISMATCH);
        }

        return jwtUtil.createToken(user.getUserId(), user.getLoginId(), user.getRole());
    }
}
