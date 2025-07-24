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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> stringRedisTemplate;

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

        // 1. 유저 검증
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.INFO_MISMATCH));

        // 2. 소프트 삭제 검증
        if(user.isDeleted()){
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        // 3. 비밀번호 검증
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.INFO_MISMATCH);
        }

        // 4. 토큰 생성
        TokenResponse token = jwtUtil.createToken(user.getUserId(), user.getLoginId(), user.getRole());

        // 5. Refresh Token을 Redis에 저장
        String redis = "refreshToken:" + user.getUserId();
        String refreshTokenField = "refreshToken";
        String refreshToken = token.getRefreshToken();
        Long refreshTokenTime = jwtUtil.getRefreshTokenTime();

        // 5.1 이미 들어 있으면 값 초기화
        Boolean deleted = stringRedisTemplate.delete(redis);
        if (Boolean.TRUE.equals(deleted)) {
            log.info("기존 Refresh Token 삭제 완료 (userId: {}): {}", user.getUserId(), redis);
        } else {
            log.info("기존 Refresh Token이 Redis에 존재하지 않음 또는 삭제 실패 (userId: {}): {}", user.getUserId(), redis);
        }

        // 5.2 새로운 Refresh Token 및 추가 정보 해시 테이블에 저장
        Map<String, String> hashData = new HashMap<>();
        hashData.put(refreshTokenField, redis);
        hashData.put("loginId", user.getLoginId()); // 로그인 ID도 함께 저장하여 활용 가능

        // 해시 테이블 형식으로 Redis에 저장
        stringRedisTemplate.opsForHash().putAll(redis, hashData);

        // 5.3 해시 테이블 전체에 만료 시간 설정
        stringRedisTemplate.expire(redis, refreshTokenTime, TimeUnit.MILLISECONDS);
        log.info("새로운 Refresh Token 해시 테이블 Redis에 저장 완료 (userId: {}): {} (Expires in {}ms)",
                user.getUserId(), redis, refreshTokenTime);

        // redis에 저장
        log.info("Refresh Token saved to Redis for user " + user.getLoginId() + ": " + refreshToken + " (Expires in " + refreshTokenTime + "ms)");

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
