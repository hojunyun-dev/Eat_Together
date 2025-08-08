package com.example.eat_together.global.redis;

import com.example.eat_together.domain.users.common.entity.User;
import com.example.eat_together.global.dto.TokenResponse;
import com.example.eat_together.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final JwtUtil jwtUtil;

    /**
     * Refresh Token을 Redis에 저장하는 메서드입니다.
     * 기존 토큰이 있다면 삭제하고 새로운 토큰을 저장합니다.
     *
     * @param user 로그인한 사용자 엔티티
     * @param token 발급된 Access Token과 Refresh Token을 포함하는 TokenResponse 객체
     */
    public void saveRefreshToken(User user, TokenResponse token) {
        String redisKey = "refreshToken:" + user.getUserId();
        String refreshTokenField = "refreshToken";
        String refreshToken = token.getRefreshToken();
        long refreshTokenTime = jwtUtil.getRefreshTokenTime();

        // 1. 기존 Refresh Token 삭제
        Boolean deleted = stringRedisTemplate.delete(redisKey);
        if (Boolean.TRUE.equals(deleted)) {
            log.info("기존 Refresh Token 삭제 완료 (userId: {}): {}", user.getUserId(), redisKey);
        } else {
            log.info("기존 Refresh Token이 Redis에 존재하지 않음 또는 삭제 실패 (userId: {}): {}", user.getUserId(), redisKey);
        }

        // 2. 'Bearer ' 접두사 제거 및 공백 제거
        String cleanRefreshTokenForRedis;
        if (StringUtils.hasText(refreshToken) && refreshToken.startsWith(JwtUtil.BEARER_PREFIX)) {
            cleanRefreshTokenForRedis = refreshToken.substring(JwtUtil.BEARER_PREFIX.length()).trim();
        } else {
            cleanRefreshTokenForRedis = refreshToken.trim();
        }

        // 3. 새로운 Refresh Token 및 추가 정보 해시 테이블에 저장
        Map<String, String> hashData = new HashMap<>();
        hashData.put(refreshTokenField, cleanRefreshTokenForRedis);
        // 소셜 로그인 사용자의 경우 loginId가 null일 수 있으므로, email을 대체 값으로 사용하도록 고려할 수 있습니다.
        hashData.put("loginId", user.getLoginId() != null ? user.getLoginId() : user.getEmail()); // loginId가 null이면 email 사용

        stringRedisTemplate.opsForHash().putAll(redisKey, hashData);

        // 4. 해시 테이블 전체에 만료 시간 설정
        stringRedisTemplate.expire(redisKey, refreshTokenTime, TimeUnit.MILLISECONDS);
        log.info("새로운 Refresh Token 해시 테이블 Redis에 저장 완료 (userId: {}): {} (Expires in {}ms)",
                user.getUserId(), redisKey, refreshTokenTime);
        log.info("Refresh Token saved to Redis for user " + user.getLoginId() + ": " + refreshToken + " (Expires in " + refreshTokenTime + "ms)");
    }

    /**
     * Redis에서 Refresh Token을 조회하는 메서드 (필요시 추가)
     * @param userId 사용자 ID
     * @return 저장된 Refresh Token 문자열 (없으면 null)
     */
    public String getRefreshToken(Long userId) {
        String redisKey = "refreshToken:" + userId;
        String refreshTokenField = "refreshToken";
        return (String) stringRedisTemplate.opsForHash().get(redisKey, refreshTokenField);
    }

    /**
     * Redis에서 Refresh Token을 삭제하는 메서드
     * @param userId 사용자 ID
     */
    public void deleteRefreshToken(Long userId) {
        String redisKey = "refreshToken:" + userId;
        Boolean deleted = stringRedisTemplate.delete(redisKey);
        if (Boolean.TRUE.equals(deleted)) {
            log.info("Redis에 등록된 Refresh Token 삭제 완료 (userId: {}): {}", userId, redisKey);
        } else {
            log.warn("Redis에 등록된 Refresh Token이 존재하지 않거나 삭제 실패 (userId: {}): {}", userId, redisKey);
        }
    }
}
