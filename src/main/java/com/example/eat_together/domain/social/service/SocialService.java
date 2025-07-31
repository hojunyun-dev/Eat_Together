package com.example.eat_together.domain.social.service;

import com.example.eat_together.domain.social.helper.SocialLoginType;
import com.example.eat_together.domain.users.common.entity.User;
import com.example.eat_together.domain.users.user.repository.UserRepository;
import com.example.eat_together.global.dto.TokenResponse;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.redis.RedisService;
import com.example.eat_together.global.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialService {

    private final GoogleOauth googleOauth;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisService redisService;
    public String request(SocialLoginType socialLoginType) {
        if (socialLoginType == SocialLoginType.GOOGLE) {
            String redirectUrl = googleOauth.getOauthRedirectURL();
            log.info("SocialService에서 Google Redirect URL 반환: {}", redirectUrl);
            return redirectUrl;
        }
        log.error("지원하지 않는 소셜 로그인 타입입니다: {}", socialLoginType);
        throw new IllegalArgumentException("지원하지 않는 소셜 로그인 타입입니다: " + socialLoginType);
    }

    @Transactional // 사용자 저장/업데이트 시 트랜잭션 필요
    public TokenResponse requestAccessToken(SocialLoginType socialLoginType, String code) {
        if (socialLoginType == SocialLoginType.GOOGLE) {
            // 1. Google로부터 Access Token 및 사용자 정보 (ID Token 파싱) 가져오기
            Map<String, Object> userInfo = googleOauth.requestAccessTokenAndGetUserInfo(code);
            log.info("Google로부터 받은 사용자 정보: {}", userInfo);

            // Google ID Token에서 필요한 사용자 정보 추출
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");

            // 닉네임은 이메일 앞부분으로 임시 생성
            String nickname = email.split("@")[0];

            User user;

            try {
                // findByEmail이 여러 결과를 반환할 경우 IncorrectResultSizeDataAccessException 발생
                Optional<User> optionalUser = userRepository.findByEmail(email);

                if (optionalUser.isEmpty()) {
                    // 사용자 없으면 회원가입
                    user = User.socialLogin()
                            .email(email)
                            .nickname(nickname)
                            .name(name != null ? name : nickname)
                            .socialLoginType(socialLoginType)
                            .build();

                    userRepository.save(user);
                    log.info("새로운 사용자 가입: {}", email);
                } else {
                    // 사용자 있으면 로그인
                    user = optionalUser.get();
                    log.info("기존 사용자 로그인: {}", email);
                    user.updateSocialProfile(name, nickname);
                    userRepository.save(user);
                }
            } catch (IncorrectResultSizeDataAccessException e) {
                // 이메일로 여러 사용자가 조회된 경우 (데이터베이스에 중복 데이터 존재)
                log.error("이메일 '{}'로 중복된 사용자가 발견되었습니다.", email, e);
                throw new CustomException(ErrorCode.DUPLICATE_USER_EMAIL); // 사용자 정의 예외 발생
            }

            // 3. 애플리케이션 JWT 토큰 발급
            String loginIdForToken = user.getLoginId() != null ? user.getLoginId() : user.getEmail();

            TokenResponse appJwtToken = jwtUtil.createToken(user.getUserId(), loginIdForToken, user.getRole());
            log.info("애플리케이션 JWT 토큰 발급 완료: {}", appJwtToken);

            redisService.saveRefreshToken(user,appJwtToken);

            // 클라이언트로 JWT 토큰을 반환합니다.
            return appJwtToken;

        }
        log.error("지원하지 않는 소셜 로그인 타입입니다: {}", socialLoginType);
        throw new IllegalArgumentException("지원하지 않는 소셜 로그인 타입입니다: " + socialLoginType);
    }
}
