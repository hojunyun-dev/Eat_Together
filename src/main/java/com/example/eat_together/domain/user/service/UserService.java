package com.example.eat_together.domain.user.service;

import com.example.eat_together.domain.user.dto.request.PasswordRequestDto;
import com.example.eat_together.domain.user.dto.request.ReissueRequestDto;
import com.example.eat_together.domain.user.dto.request.UpdateUserInfoRequestDto;
import com.example.eat_together.domain.user.dto.response.UserResponseDto;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.global.dto.TokenResponse;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> stringRedisTemplate;

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
    public Page<UserResponseDto> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponseDto::toDto);
    }

    // 마이페이지 조회
    public UserResponseDto findMyProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserResponseDto(user);
    }

    // 유저 삭제
    @Transactional
    public void deleteUser(PasswordRequestDto request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.PASSWORD_WRONG);
        }

        user.deleteUser();
    }

    // 토큰 재발급
    @Transactional
    public String reissue(ReissueRequestDto request) {

        String refreshToken = request.getRefreshToken();

        // 1. Refresh Token 자체의 유효성 검사 및 클레임 추출
        Claims claims = jwtUtil.extractClaims(refreshToken);

        Long userId = Long.parseLong(claims.getSubject());
        String loginId = claims.get("loginId", String.class);
        log.info("재발급 요청 Refresh Token Claims: userId={}, loginId={}", userId, loginId);

        String hashKey = "refreshToken:" + userId;
        String refreshTokenField = "refreshToken";

        // 2. Redis에 저장된 Refresh Token과 일치하는지 확인
        String storedRefreshToken = (String) stringRedisTemplate.opsForHash().get(hashKey, refreshTokenField);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            // Redis에 없거나 일치하지 않으면 유효하지 않은 토큰으로 간주
            log.warn("유효하지 않거나 일치하지 않는 Refresh Token: userId={}, hashKey={}, stored={}, incoming={}",
                    userId, hashKey, storedRefreshToken, refreshToken);
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3. Refresh Token 자체의 유효성 (만료 여부) 다시 확인
        // Redis에 저장된 토큰이 만료되었을 때 Redis에서 삭제하는 로직은 유효
        if (!jwtUtil.isValidToken(refreshToken)) {
            stringRedisTemplate.delete(hashKey);
            log.warn("만료된 Refresh Token 발견: userId={}. Redis에서 삭제.", userId);
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 4. 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INFO_MISMATCH));

        // 5. 새로운 Access Token 및 Refresh Token 생성
        TokenResponse newTokenResponse = jwtUtil.createToken(user.getUserId(), user.getLoginId(), user.getRole());

        String accessToken = newTokenResponse.getAccessToken();

        // 6. 새로운 Refresh Token으로 Redis 해시 테이블 업데이트
        Map<String, String> newHashData = new HashMap<>();
        newHashData.put(refreshTokenField, newTokenResponse.getRefreshToken());
        newHashData.put("loginId", user.getLoginId());

        stringRedisTemplate.opsForHash().putAll(hashKey, newHashData);

        // 7. 해시 테이블 전체 만료 시간 재설정 (재발급 시마다 만료 시간 연장)
        stringRedisTemplate.expire(hashKey, jwtUtil.getRefreshTokenTime(), TimeUnit.MILLISECONDS);

        log.info("새로운 Access Token 및 Refresh Token 발급 완료 (userId: {}), Redis 업데이트: {}", user.getUserId(), hashKey);

        return accessToken;
    }

    @Transactional
    public UserResponseDto restoration(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Soft delete 검증
        if(!user.isDeleted()){
            throw new CustomException(ErrorCode.USER_NOT_DELETE);
        }

        // 복구
        user.restoration();

        return new UserResponseDto(user);
    }
}
