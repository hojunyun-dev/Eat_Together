package com.example.eat_together.domain.user.service;

import com.example.eat_together.domain.user.dto.request.*;
import com.example.eat_together.domain.user.dto.response.UserResponseDto;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.entity.UserRole;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.dto.TokenResponse;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.fixture.UserTestFixture;
import com.example.eat_together.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private HashOperations<String, String, String> hashOperations;
    @BeforeEach
    void setUp() {
        reset(userRepository, passwordEncoder, jwtUtil, stringRedisTemplate, hashOperations);
    }

    @Test
    @DisplayName("비밀번호 변경 성공 테스트")
    void changePassword_Success() {
        // Given
        Long userId = 1L;
        User mockUser = mock(User.class);
        ChangePasswordRequestDto request = new ChangePasswordRequestDto(mockUser.getPassword(), "newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getPassword()).thenReturn("encodedOldPassword");
        when(passwordEncoder.matches(request.getOldPassword(), "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encodedNewPassword");

        doNothing().when(mockUser).updatePassword(anyString());

        // When
        userService.changePassword(userId, request);

        // Then
        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(request.getOldPassword(), "encodedOldPassword");
        verify(passwordEncoder, times(1)).encode(request.getNewPassword());
        verify(mockUser, times(1)).updatePassword("encodedNewPassword");
    }

    @Test
    @DisplayName("비밀번호 변경 실패 테스트 - 유저를 찾을 수 없음")
    void changePassword_UserNotFound() {
        // Given
        Long userId = 1L;
        ChangePasswordRequestDto request = new ChangePasswordRequestDto("oldPassword", "newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.empty()); // 유저 없음

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                userService.changePassword(userId, request));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());


        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 테스트 - 현재 비밀번호 불일치")
    void changePassword_PasswordMismatch() {
        // Given
        Long userId = 1L;
        ChangePasswordRequestDto request = new ChangePasswordRequestDto("wrongOldPassword", "newPassword");
        User mockUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getPassword()).thenReturn("encodedCorrectOldPassword");
        when(passwordEncoder.matches(request.getOldPassword(), "encodedCorrectOldPassword")).thenReturn(false);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                userService.changePassword(userId, request));
        assertEquals(ErrorCode.PASSWORD_WRONG, exception.getErrorCode());


        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(request.getOldPassword(), "encodedCorrectOldPassword");
        verify(passwordEncoder, never()).encode(anyString());
        verify(mockUser, never()).updatePassword(anyString());
    }

    @Test
    @DisplayName("개인 정보 수정 성공 테스트")
    void updateProfile_Success() {

        // Given
        Long userId = 1L;
        UpdateUserInfoRequestDto request = new UpdateUserInfoRequestDto("newNickname", "newEmail", "newAddress");
        User mockUser = mock(User.class);
        User savedUser = UserTestFixture.유저_생성(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        doNothing().when(mockUser).updateProfile(request);
        when(userRepository.save(mockUser)).thenReturn(savedUser);

        // When
        UserResponseDto result = userService.updateProfile(userId, request);

        // Then
        assertNotNull(result);
        assertEquals(savedUser.getUserId(), result.getUserId());

        verify(userRepository, times(1)).findById(userId);
        verify(mockUser, times(1)).updateProfile(request);
        verify(userRepository, times(1)).save(mockUser);

    }

    @Test
    @DisplayName("개인 정보 수정 실패 테스트 - 유저를 찾을 수 없음")
    void updateProfile_UserNotFound() {
        // Given
        Long userId = 1L;
        UpdateUserInfoRequestDto request = new UpdateUserInfoRequestDto("newNickname", "newEmail", "newAddress");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                userService.updateProfile(userId, request));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("유저 단건 조회 성공 테스트")
    void findUser_Success() {
        // Given
        Long userId = 1L;
        User mockUser = UserTestFixture.유저_생성(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // When
        UserResponseDto result = userService.findUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(mockUser.getUserId(), result.getUserId());
        assertEquals(mockUser.getLoginId(), result.getLoginId());

        assertEquals(mockUser.getName(), result.getName());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("유저 단건 조회 실패 테스트 - 유저를 찾을 수 없음")
    void findUser_UserNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                userService.findUser(userId));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("유저 전체 조회 성공 테스트")
    void findAllUsers_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        User user1 = UserTestFixture.유저_생성(1L);
        User user2 = UserTestFixture.유저_생성(2L);
        Page<User> userPage = new PageImpl<>(Arrays.asList(user1, user2), pageable, 2);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<UserResponseDto> resultPage = userService.findAllUsers(pageable);

        // Then
        assertNotNull(resultPage);
        assertEquals(2, resultPage.getTotalElements());
        assertEquals(2, resultPage.getContent().size());
        assertEquals(user1.getUserId(), resultPage.getContent().get(0).getUserId());
        assertEquals(user2.getUserId(), resultPage.getContent().get(1).getUserId());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("유저 전체 조회 성공 테스트 - 빈 페이지")
    void findAllUsers_EmptyPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        Page<UserResponseDto> resultPage = userService.findAllUsers(pageable);

        // Then
        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        assertEquals(0, resultPage.getTotalElements());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("마이페이지 조회 성공 테스트")
    void findMyProfile_Success() {
        // Given
        Long userId = 1L;
        User mockUser = UserTestFixture.유저_생성(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // When
        UserResponseDto result = userService.findMyProfile(userId);

        // Then
        assertNotNull(result);
        assertEquals(mockUser.getUserId(), result.getUserId());
        assertEquals(mockUser.getLoginId(), result.getLoginId());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("마이페이지 조회 실패 테스트 - 유저를 찾을 수 없음")
    void findMyProfile_UserNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                userService.findMyProfile(userId));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("유저 삭제 성공 테스트")
    void deleteUser_Success() {
        // Given
        Long userId = 1L;
        PasswordRequestDto request = new PasswordRequestDto("correctPassword");
        User mockUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getPassword()).thenReturn("encodedCorrectPassword");
        when(passwordEncoder.matches(request.getPassword(), "encodedCorrectPassword")).thenReturn(true);
        doNothing().when(mockUser).deleteUser(); // deleteUser는 void 메소드

        // When
        userService.deleteUser(request, userId);

        // Then
        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(request.getPassword(), "encodedCorrectPassword");
        verify(mockUser, times(1)).deleteUser();
    }

    @Test
    @DisplayName("유저 삭제 실패 테스트 - 유저를 찾을 수 없음")
    void deleteUser_UserNotFound() {
        // Given
        Long userId = 1L;
        PasswordRequestDto request = new PasswordRequestDto("password");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                userService.deleteUser(request, userId));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("유저 삭제 실패 테스트 - 비밀번호 불일치")
    void deleteUser_PasswordMismatch() {
        // Given
        Long userId = 1L;
        PasswordRequestDto request = new PasswordRequestDto("wrongPassword");
        User mockUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getPassword()).thenReturn("encodedCorrectPassword");
        when(passwordEncoder.matches(request.getPassword(), "encodedCorrectPassword")).thenReturn(false);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                userService.deleteUser(request, userId));
        assertEquals(ErrorCode.PASSWORD_WRONG, exception.getErrorCode());

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(request.getPassword(), "encodedCorrectPassword");
        verify(mockUser, never()).deleteUser();
    }

    @Test
    @DisplayName("토큰 재발급 성공 테스트")
    void reissue_Success() {
        // Given
        String oldRefreshToken = "validRefreshToken";
        Long userId = 1L;
        String loginId = "testUser";
        String hashKey = "refreshToken:" + userId;
        String refreshTokenField = "refreshToken";
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        ReissueRequestDto request = new ReissueRequestDto(oldRefreshToken);
        User mockUser = mock(User.class);

        // 1. JWT 유효성 검사 및 클레임 추출 Stubbing
        Claims mockClaims = Jwts.claims().setSubject(String.valueOf(userId));
        mockClaims.put("loginId", loginId);
        when(jwtUtil.extractClaims(oldRefreshToken)).thenReturn(mockClaims);
        when(jwtUtil.isValidToken(oldRefreshToken)).thenReturn(true);

        // 2. Redis 조회 Stubbing
        doReturn(hashOperations).when(stringRedisTemplate).opsForHash();
        when(hashOperations.get(hashKey, refreshTokenField)).thenReturn(oldRefreshToken);

        // 3. 사용자 정보 조회 Stubbing
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getUserId()).thenReturn(userId);
        when(mockUser.getLoginId()).thenReturn(loginId);
        when(mockUser.getRole()).thenReturn(UserRole.valueOf("USER"));

        // 4. 새로운 토큰 생성 Stubbing
        TokenResponse newTokenResponse = TokenResponse.of(newAccessToken, newRefreshToken);
        when(jwtUtil.createToken(userId, loginId, UserRole.valueOf("USER"))).thenReturn(newTokenResponse);
        when(jwtUtil.getRefreshTokenTime()).thenReturn(3600000L); // 리프레시 토큰 유효 시간

        // 5. Redis 업데이트 Stubbing
        doNothing().when(hashOperations).putAll(eq(hashKey), anyMap());
        when(stringRedisTemplate.expire(eq(hashKey), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

        // When
        String resultAccessToken = userService.reissue(request);

        // Then
        assertNotNull(resultAccessToken);
        assertEquals(newAccessToken, resultAccessToken);

        // 검증
        verify(jwtUtil, times(1)).extractClaims(oldRefreshToken);
        verify(jwtUtil, times(1)).isValidToken(oldRefreshToken);
        verify(stringRedisTemplate, times(2)).opsForHash();
        verify(hashOperations, times(1)).get(hashKey, refreshTokenField);
        verify(userRepository, times(1)).findById(userId);
        verify(jwtUtil, times(1)).createToken(userId, loginId, UserRole.valueOf("USER"));
        verify(hashOperations, times(1)).putAll(eq(hashKey), anyMap());
        verify(stringRedisTemplate, times(1)).expire(eq(hashKey), anyLong(), eq(TimeUnit.MILLISECONDS));
        verify(stringRedisTemplate, never()).delete(anyString()); // 만료되지 않았으므로 delete 호출 안 됨
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트 - Redis에 Refresh Token이 없음")
    void reissue_InvalidRefreshToken_NotFoundInRedis() {
        // Given
        String oldRefreshToken = "invalidRefreshToken";
        Long userId = 1L;
        String loginId = "testUser";
        String hashKey = "refreshToken:" + userId;
        String refreshTokenField = "refreshToken";

        ReissueRequestDto request = new ReissueRequestDto(oldRefreshToken);

        Claims mockClaims = Jwts.claims().setSubject(String.valueOf(userId));
        mockClaims.put("loginId", loginId);
        when(jwtUtil.extractClaims(oldRefreshToken)).thenReturn(mockClaims);

        doReturn(hashOperations).when(stringRedisTemplate).opsForHash();
        when(hashOperations.get(hashKey, refreshTokenField)).thenReturn(null);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                userService.reissue(request));
        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());

        // 검증
        verify(jwtUtil, times(1)).extractClaims(oldRefreshToken);
        verify(jwtUtil, never()).isValidToken(anyString());
        verify(stringRedisTemplate, times(1)).opsForHash();
        verify(hashOperations, times(1)).get(hashKey, refreshTokenField);
        verify(userRepository, never()).findById(anyLong()); // 사용자 조회 호출 안 됨
        verify(jwtUtil, never()).createToken(anyLong(), anyString(), any());
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트 - Redis의 Refresh Token과 불일치")
    void reissue_InvalidRefreshToken_MismatchInRedis() {
        // Given
        String oldRefreshToken = "incomingRefreshToken";
        String storedRefreshToken = "differentStoredRefreshToken";
        Long userId = 1L;
        String loginId = "testUser";
        String hashKey = "refreshToken:" + userId;
        String refreshTokenField = "refreshToken";

        ReissueRequestDto request = new ReissueRequestDto(oldRefreshToken);

        Claims mockClaims = Jwts.claims().setSubject(String.valueOf(userId));
        mockClaims.put("loginId", loginId);
        when(jwtUtil.extractClaims(oldRefreshToken)).thenReturn(mockClaims);

        doReturn(hashOperations).when(stringRedisTemplate).opsForHash();
        when(hashOperations.get(hashKey, refreshTokenField)).thenReturn(storedRefreshToken); // 불일치

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                userService.reissue(request));
        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());

        // 검증
        verify(jwtUtil, times(1)).extractClaims(oldRefreshToken);
        verify(jwtUtil, never()).isValidToken(anyString());
        verify(stringRedisTemplate, times(1)).opsForHash();
        verify(hashOperations, times(1)).get(hashKey, refreshTokenField);
        verify(userRepository, never()).findById(anyLong());
        verify(jwtUtil, never()).createToken(anyLong(), anyString(), any());
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트 - Refresh Token 만료")
    void reissue_RefreshTokenExpired() {
        // Given
        String oldRefreshToken = "expiredRefreshToken";
        Long userId = 1L;
        String loginId = "testUser";
        String hashKey = "refreshToken:" + userId;
        String refreshTokenField = "refreshToken";

        ReissueRequestDto request = new ReissueRequestDto(oldRefreshToken);

        Claims mockClaims = Jwts.claims().setSubject(String.valueOf(userId));
        mockClaims.put("loginId", loginId);
        when(jwtUtil.extractClaims(oldRefreshToken)).thenReturn(mockClaims);

        doReturn(hashOperations).when(stringRedisTemplate).opsForHash();
        when(hashOperations.get(hashKey, refreshTokenField)).thenReturn(oldRefreshToken);

        when(stringRedisTemplate.delete(hashKey)).thenReturn(true);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                userService.reissue(request));
        assertEquals(ErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());

        // 검증
        verify(jwtUtil, times(1)).extractClaims(oldRefreshToken);
        verify(jwtUtil, times(1)).isValidToken(oldRefreshToken);
        verify(stringRedisTemplate, times(1)).opsForHash();
        verify(hashOperations, times(1)).get(hashKey, refreshTokenField);
        verify(stringRedisTemplate, times(1)).delete(hashKey);
        verify(userRepository, never()).findById(anyLong());
        verify(jwtUtil, never()).createToken(anyLong(), anyString(), any());
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트 - 사용자 정보를 찾을 수 없음 (INFO_MISMATCH)")
    void reissue_UserNotFound() {
        // Given
        String oldRefreshToken = "validRefreshToken";
        Long userId = 1L;
        String loginId = "testUser";
        String hashKey = "refreshToken:" + userId;
        String refreshTokenField = "refreshToken";

        ReissueRequestDto request = new ReissueRequestDto(oldRefreshToken);

        Claims mockClaims = Jwts.claims().setSubject(String.valueOf(userId));
        mockClaims.put("loginId", loginId);
        when(jwtUtil.extractClaims(oldRefreshToken)).thenReturn(mockClaims);
        when(jwtUtil.isValidToken(oldRefreshToken)).thenReturn(true);

        doReturn(hashOperations).when(stringRedisTemplate).opsForHash();
        when(hashOperations.get(hashKey, refreshTokenField)).thenReturn(oldRefreshToken);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                userService.reissue(request));
        // ⭐ 재발급 로직에서 사용자 없을 때 INFO_MISMATCH를 던지므로 그대로 따릅니다.
        assertEquals(ErrorCode.INFO_MISMATCH, exception.getErrorCode());

        // 검증
        verify(jwtUtil, times(1)).extractClaims(oldRefreshToken);
        verify(jwtUtil, times(1)).isValidToken(oldRefreshToken);
        verify(stringRedisTemplate, times(1)).opsForHash();
        verify(hashOperations, times(1)).get(hashKey, refreshTokenField);
        verify(userRepository, times(1)).findById(userId); // 사용자 조회 호출
        verify(jwtUtil, never()).createToken(anyLong(), anyString(), any());
    }

    @Test
    @DisplayName("계정 복구 성공 테스트")
    void restoration_Success() {
        // Given
        Long userId = 1L;
        User mockUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isDeleted()).thenReturn(true);
        doNothing().when(mockUser).restoration();

        // When
        UserResponseDto result = userService.restoration(userId);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(mockUser, times(1)).isDeleted();
        verify(mockUser, times(1)).restoration();
    }

    @Test
    @DisplayName("계정 복구 실패 테스트 - 유저를 찾을 수 없음")
    void restoration_UserNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                userService.restoration(userId));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class)); // save 호출 안 됨
    }

    @Test
    @DisplayName("계정 복구 실패 테스트 - 이미 복구된(삭제되지 않은) 유저")
    void restoration_UserNotDeleted() {
        // Given
        Long userId = 1L;
        User mockUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isDeleted()).thenReturn(false); // 이미 삭제되지 않은 상태

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                userService.restoration(userId));
        assertEquals(ErrorCode.USER_NOT_DELETE, exception.getErrorCode());

        verify(userRepository, times(1)).findById(userId);
        verify(mockUser, times(1)).isDeleted();
        verify(mockUser, never()).restoration(); // 복구 메소드 호출 안 됨
    }
}