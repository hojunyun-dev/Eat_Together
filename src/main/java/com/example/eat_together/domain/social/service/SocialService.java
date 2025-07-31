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
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RedisService redisService;

    public String request(SocialLoginType socialLoginType) {
        if (socialLoginType == SocialLoginType.GOOGLE) {
            String redirectUrl = googleOauth.getOauthRedirectURL();
            log.info("SocialService에서 Google Redirect URL 반환: {}", redirectUrl);
            return redirectUrl;
        } else if (socialLoginType == SocialLoginType.KAKAO) { // KAKAO 로그인 요청 처리
            String redirectUrl = kakaoOauth.getOauthRedirectURL();
            log.info("SocialService에서 Kakao Redirect URL 반환: {}", redirectUrl);
            return redirectUrl;
        }

        log.error("지원하지 않는 소셜 로그인 타입입니다: {}", socialLoginType);
        throw new IllegalArgumentException("지원하지 않는 소셜 로그인 타입입니다: " + socialLoginType);
    }

    @Transactional // 사용자 저장/업데이트 시 트랜잭션 필요
    public TokenResponse requestAccessToken(SocialLoginType socialLoginType, String code) {
        Map<String, Object> userInfo;
        if (socialLoginType == SocialLoginType.GOOGLE) {
            userInfo = googleOauth.requestAccessTokenAndGetUserInfo(code);
        } else if (socialLoginType == SocialLoginType.KAKAO) { // KAKAO Access Token 및 사용자 정보 가져오기
            userInfo = kakaoOauth.requestAccessTokenAndGetUserInfo(code);
        } else {
            log.error("지원하지 않는 소셜 로그인 타입입니다 ::: {}", socialLoginType);
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인 타입입니다: " + socialLoginType);
        }

        log.info("{}로부터 받은 사용자 정보: {}", socialLoginType, userInfo);

        String email = (String) userInfo.get("email");
        // Google은 'name' 필드, Kakao는 'nickname' 또는 'name' (profile.nickname)
        String name = (String) userInfo.get("name");
        String nickname = (String) userInfo.get("nickname"); // 카카오에서 닉네임은 'profile.nickname'

        // 카카오의 경우 'name' 필드가 없을 수 있으므로 닉네임을 기본으로 사용
        if (name == null && nickname != null) {
            name = nickname;
        } else if (name == null && email != null) {
            name = email.split("@")[0]; // 이름도 닉네임도 없으면 이메일 앞부분 사용
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
                // 사용자 없으면 회원가입
                user = User.socialLogin() // User.socialLogin() 빌더 사용
                        .email(email)
                        .nickname(nickname) // 임시 닉네임 설정
                        .name(name)
                        .socialLoginType(socialLoginType)
                        // profilePictureUrl 필드가 없으므로 빌더에서 설정하지 않음
                        .build();

                userRepository.save(user);
                log.info("새로운 {} 사용자 가입: {}", socialLoginType, email);
            } else {
                // 사용자 있으면 로그인
                user = optionalUser.get();
                log.info("기존 {} 사용자 로그인: {}", socialLoginType, email);
                // 소셜 로그인 사용자의 프로필 정보 업데이트 (이름, 닉네임 변경 시)
                // User 엔티티의 updateSocialProfile 메서드가 name, nickname만 받도록 수정 필요
                user.updateSocialProfile(name, nickname); // <-- picture 인자 제거
                userRepository.save(user);
            }
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("이메일 '{}'로 중복된 사용자가 발견되었습니다.", email, e);
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
