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
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialService {

    private final GoogleOauth googleOauth;
    private final KakaoOauth kakaoOauth;
    private final NaverOauth naverOauth;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RedisService redisService;

    public String request(SocialLoginType socialLoginType) {
        if (socialLoginType == SocialLoginType.GOOGLE) {
            String redirectUrl = googleOauth.getOauthRedirectURL();
            log.info("SocialService에서 Google Redirect URL 반환: {}", redirectUrl);
            return redirectUrl;
        } else if (socialLoginType == SocialLoginType.KAKAO) {
            String redirectUrl = kakaoOauth.getOauthRedirectURL();
            log.info("SocialService에서 Kakao Redirect URL 반환: {}", redirectUrl);
            return redirectUrl;
        } else if (socialLoginType == SocialLoginType.NAVER) {
            String redirectUrl = naverOauth.getOauthRedirectURL();
            log.info("SocialService에서 Naver Redirect URL 반환: {}", redirectUrl);
            return redirectUrl;
        }

        log.error("지원하지 않는 소셜 로그인 타입입니다: {}", socialLoginType);
        throw new IllegalArgumentException("지원하지 않는 소셜 로그인 타입입니다: " + socialLoginType);
    }

    @Transactional
    public TokenResponse requestAccessToken(SocialLoginType socialLoginType, String code) {
        Map<String, Object> userInfo;
        if (socialLoginType == SocialLoginType.GOOGLE) {
            userInfo = googleOauth.requestAccessTokenAndGetUserInfo(code);
        } else if (socialLoginType == SocialLoginType.KAKAO) {
            userInfo = kakaoOauth.requestAccessTokenAndGetUserInfo(code);
        } else if (socialLoginType == SocialLoginType.NAVER) {
            userInfo = naverOauth.requestAccessTokenAndGetUserInfo(code);
        } else {
            log.error("지원하지 않는 소셜 로그인 타입입니다 ::: {}", socialLoginType);
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인 타입입니다: " + socialLoginType);
        }

        log.info("{}로부터 받은 사용자 정보: {}", socialLoginType, userInfo);

        String email = (String) userInfo.get("email");

        String name = (String) userInfo.get("name");
        String nickname = (String) userInfo.get("nickname");

        // 카카오의 경우 'name' 필드가 없을 수 있으므로 닉네임을 기본으로 사용
        if (name == null && nickname != null) {
            name = nickname;
        } else if (name == null && email != null) {
            name = email.split("@")[0];
        } else if (name == null) {
            name = "소셜사용자"; // 최소한의 이름
        }

        // 닉네임이 없으면 이메일 앞부분으로 임시 생성
        if (nickname == null && email != null) {
            nickname = email.split("@")[0];
        } else if (nickname == null) {
            nickname = "소셜닉네임"; // 최소한의 닉네임
        }

        User user;

        try {
            Optional<User> optionalUser = userRepository.findByEmail(email);

            if (optionalUser.isEmpty()) {
                // 새로운 사용자 회원가입
                user = User.socialLogin()
                        .email(email)
                        .nickname(nickname)
                        .name(name)
                        .socialLoginType(socialLoginType)
                        .build();

                userRepository.save(user);
                log.info("새로운 {} 사용자 가입: 이메일={}, 닉네임={}, 이름={}, 저장된 소셜타입={}",
                        socialLoginType, email, nickname, name, user.getSocialLoginType());
            } else {
                // 기존 사용자 처리
                user = optionalUser.get();
                log.info("기존 사용자 발견: 이메일={}, DB에서 조회된 소셜타입={}, 현재 시도 소셜타입={}",
                        email, user.getSocialLoginType(), socialLoginType);

                // 일반 계정으로 가입된 이메일로 소셜 로그인 시도하는 경우
                if (user.getSocialLoginType() == null) {
                    log.error("이메일 '{}'는 이미 일반 계정으로 가입되어 있습니다. 현재 시도: {}", email, socialLoginType);
                    throw new CustomException(ErrorCode.EMAIL_ALREADY_REGISTERED_WITH_OTHER_SOCIAL_TYPE);
                }
                // 이미 다른 소셜 계정으로 가입된 이메일로 현재 소셜 로그인 시도하는 경우
                else if (user.getSocialLoginType() != socialLoginType) {
                    log.error("이메일 '{}'는 이미 다른 소셜 계정({})으로 가입되어 있습니다. 현재 시도: {}",
                            email, user.getSocialLoginType(), socialLoginType);
                    throw new CustomException(ErrorCode.EMAIL_ALREADY_REGISTERED_WITH_OTHER_SOCIAL_TYPE);
                }
                // 같은 소셜 타입으로 재로그인하는 경우
                else {
                    log.info("기존 {} 사용자 재로그인: 이메일={}, 닉네임={}, 이름={}",
                            socialLoginType, email, nickname, name);
                    user.updateSocialProfile(name, nickname);
                    userRepository.save(user);
                }
            }
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("이메일 '{}'로 중복된 사용자가 데이터베이스에 발견되었습니다. (DB 데이터 무결성 문제)", email, e);
            throw new CustomException(ErrorCode.DUPLICATE_USER_EMAIL);
        }

        // 3. 애플리케이션 JWT 토큰 발급
        String loginIdForToken = user.getLoginId() != null ? user.getLoginId() : user.getEmail();

        TokenResponse appJwtToken = jwtUtil.createToken(user.getUserId(), loginIdForToken, user.getRole());
        log.info("애플리케이션 JWT 토큰 발급 완료: {}", appJwtToken);

        redisService.saveRefreshToken(user, appJwtToken);

        return appJwtToken;
    }
}
