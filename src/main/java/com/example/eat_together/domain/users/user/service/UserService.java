package com.example.eat_together.domain.users.user.service;

import com.example.eat_together.domain.users.common.dto.request.*;
import com.example.eat_together.domain.users.common.dto.response.UserInfoResponseDto;
import com.example.eat_together.domain.users.common.dto.response.UserResponseDto;
import com.example.eat_together.domain.users.common.entity.User;
import com.example.eat_together.global.dto.TokenResponse;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.domain.users.user.repository.UserRepository;
import com.example.eat_together.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
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

        if(!(user.getSocialLoginType() == null)){
            throw new CustomException(ErrorCode.SOCIAL_NOCHANGE_PASSWORD);
        }
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

    // 삭제된 유저 복구
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

    // 유저 닉네임 or 이름 검색기능
    public List<UserInfoResponseDto> findByUserInfo(UserSearchRequestDto request) {

        List<User> foundUsers;

        // 1. 이름으로 검색 (이름이 제공된 경우)
        if (request.getName() != null && !request.getName().isEmpty()) {
            foundUsers = userRepository.findByName(request.getName());
        }

        // 2. 닉네임으로 검색 (이름이 없고 닉네임이 제공된 경우)
        else if (request.getNickname() != null && !request.getNickname().isEmpty()) {
            foundUsers = userRepository.findByNickname(request.getNickname());
        }

        // 3. 아무런 검색 조건도 없는 경우
        else {
            throw new CustomException(ErrorCode.INVALID_SEARCH_CRITERIA);
        }

        // 검색 결과가 없는 경우
        if (foundUsers.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 조회한 User 리스트를 UserInfoResponseDto 리스트로 변환하여 리턴
        return UserInfoResponseDto.todo(foundUsers);
    }

    // index를 활용한 검색 방법
    public List<UserInfoResponseDto> findByUserInfoV2(UserSearchRequestDto request) {

        // 이름과 닉네임이 모두 비어있는 경우 예외 처리
        if ((request.getName() == null || request.getName().isEmpty()) &&
                (request.getNickname() == null || request.getNickname().isEmpty())) {
            throw new CustomException(ErrorCode.INVALID_SEARCH_CRITERIA);
        }

        // 통합된 메서드를 사용하여 데이터베이스에 한 번만 접근
        List<User> foundUsers = userRepository.findByNameOrNickname(request.getName(), request.getNickname());

        if (foundUsers.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return UserInfoResponseDto.todo(foundUsers);
    }


    // Redis를 이용한 캐싱 (기존 쿼리 로직)
    @Cacheable(value = "users_search", key = "#request.name + '_' + #request.nickname", unless = "#result.isEmpty()")
    public List<UserInfoResponseDto> findByUserInfoV3(UserSearchRequestDto request) {

        List<User> foundUsers;

        // 이름으로 검색 (이름이 제공된 경우)
        if (request.getName() != null && !request.getName().isEmpty()) {
            foundUsers = userRepository.findByName(request.getName());
        }
        // 닉네임으로 검색 (이름이 없고 닉네임이 제공된 경우)
        else if (request.getNickname() != null && !request.getNickname().isEmpty()) {
            foundUsers = userRepository.findByNickname(request.getNickname());
        }
        // 아무런 검색 조건도 없는 경우
        else {
            throw new CustomException(ErrorCode.INVALID_SEARCH_CRITERIA);
        }

        if (foundUsers.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return UserInfoResponseDto.todo(foundUsers);
    }

    // 인덱스와 Redis 캐싱을 동시에 활용한 통합 검색 기능 <- 이 버전을 선택함
    @Cacheable(value = "users_search", key = "#request.name + '_' + #request.nickname", unless = "#result.isEmpty()")
    public List<UserInfoResponseDto> findByUserInfoV4(UserSearchRequestDto request) {

        // 이름과 닉네임이 모두 비어있는 경우 예외 처리
        if ((request.getName() == null || request.getName().isEmpty()) &&
                (request.getNickname() == null || request.getNickname().isEmpty())) {
            throw new CustomException(ErrorCode.INVALID_SEARCH_CRITERIA);
        }

        // 통합된 메서드 (인덱스 활용)를 사용하여 데이터베이스에 한 번만 접근
        List<User> foundUsers = userRepository.findByNameOrNickname(request.getName(), request.getNickname());

        if (foundUsers.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return UserInfoResponseDto.todo(foundUsers);
    }
}
