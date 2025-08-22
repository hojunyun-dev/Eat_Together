package com.example.eat_together.domain.users.auth.service;

import com.example.eat_together.domain.users.common.entity.User;
import com.example.eat_together.global.dto.TokenResponse;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.redis.RedisService;
import com.example.eat_together.global.util.JwtUtil;
import com.example.eat_together.domain.users.common.dto.request.LoginRequestDto;
import com.example.eat_together.domain.users.common.dto.request.SignupRequestDto;
import com.example.eat_together.domain.users.common.dto.response.UserResponseDto;
import com.example.eat_together.domain.users.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisService redisService;

    // 회원가입
    public UserResponseDto signup(SignupRequestDto request) {

        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_USER_EMAIL);
        }
        String encodePassword = passwordEncoder.encode(request.getPassword());

        User user = new User(request, encodePassword);
        User saveUser = userRepository.save(user);

        return new UserResponseDto(saveUser);
    }

    // 로그인
    @Transactional
    public TokenResponse login(LoginRequestDto request) {

        // 1. 유저 검증
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.INFO_MISMATCH));

        // 2. 소프트 삭제 검증
        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        // 3. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INFO_MISMATCH);
        }

        // 4. 토큰 생성
        TokenResponse token = jwtUtil.createToken(user.getUserId(), user.getLoginId(), user.getRole());

        // 5. Refresh Token을 Redis에 저장
        redisService.saveRefreshToken(user,token);

        return token;
    }

    // 로그아웃
    @Transactional
    public void logout(Long userId) {
        String redis = "refreshToken:" + userId;
        Boolean deleted = stringRedisTemplate.delete(redis);

        if (Boolean.TRUE.equals(deleted)) {
            System.out.println("Redis에 등록된 User 삭제 완료 : " + userId);
        } else {
            System.out.println("Redis에 등록된 User 삭제 실패 : " + userId + "Redis에 등록되어 있지 않을 수도 있음");
        }
    }

}


