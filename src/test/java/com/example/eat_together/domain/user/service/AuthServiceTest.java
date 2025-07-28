package com.example.eat_together.domain.user.service;


import com.example.eat_together.domain.user.dto.request.LoginRequestDto;
import com.example.eat_together.domain.user.dto.request.SignupRequestDto;
import com.example.eat_together.domain.user.dto.response.UserResponseDto;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.entity.UserRole;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.dto.TokenResponse;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.fixture.UserTestFixture;
import com.example.eat_together.global.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 어노테이션 활성화
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

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
        // 각 테스트 전에 Mock 객체들의 상태를 초기화
        reset(userRepository, passwordEncoder, jwtUtil, stringRedisTemplate, hashOperations);
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_Success() {
        // Given
        User userToSave = UserTestFixture.유저_생성(null);
        User savedUser = UserTestFixture.유저_생성(1L);
        SignupRequestDto request = new SignupRequestDto(savedUser.getLoginId(),
                savedUser.getName(),
                savedUser.getPassword(),
                savedUser.getEmail(),
                savedUser.getNickname());

        // Mock 객체 동작 설정
        when(userRepository.existsByLoginId(request.getLoginId())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(savedUser.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserResponseDto result = authService.signup(request);

        // Then
        assertNotNull(result);
        assertEquals(savedUser.getUserId(), result.getUserId());
        assertEquals(savedUser.getLoginId(), result.getLoginId());
        assertEquals(savedUser.getName(), result.getName());
        assertEquals(savedUser.getEmail(), result.getEmail());
        assertEquals(savedUser.getNickname(), result.getNickname());

        // 메소드 호출 검증
        verify(userRepository, times(1)).existsByLoginId(request.getLoginId());
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 중복된 아이디")
    void signup_DuplicateUser() {

        // given
        SignupRequestDto request = new SignupRequestDto(
                "testLogin",
                "testName",
                "1q2w3e4r!",
                "test@email.com",
                "testNickname");
        // when
        when(userRepository.existsByLoginId(request.getLoginId())).thenReturn(true);

        // then
        CustomException exception = assertThrows(CustomException.class, () -> authService.signup(request));
        assertEquals(ErrorCode.DUPLICATE_USER, exception.getErrorCode());

        verify(userRepository, times(1)).existsByLoginId(request.getLoginId());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // --- Login Tests ---
    @Test
    @DisplayName("로그인 성공 테스트")
    void login_Success() {

        // given
        User foundUser = mock(User.class);

        Long userId = 1L;
        String loginId = "testLogin";
        String rawPassword = "1q2w3e4r!";
        String encodedPasswordInDb = "encodedTestPassword";

        when(foundUser.getUserId()).thenReturn(userId);
        when(foundUser.getLoginId()).thenReturn(loginId);
        when(foundUser.getPassword()).thenReturn(encodedPasswordInDb);
        when(foundUser.getRole()).thenReturn(UserRole.valueOf("USER"));
        when(foundUser.isDeleted()).thenReturn(false);

        LoginRequestDto request = new LoginRequestDto(userId,loginId, rawPassword);
        TokenResponse mockTokenResponse = TokenResponse.of("mockAccessToken", "mockRefreshToken");

        // Mock 객체 동작 설정
        doReturn(hashOperations).when(stringRedisTemplate).opsForHash();

        when(userRepository.findByLoginId(request.getLoginId())).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches(rawPassword, encodedPasswordInDb)).thenReturn(true);

        when(jwtUtil.createToken(userId, loginId, UserRole.valueOf("USER"))).thenReturn(mockTokenResponse);
        when(jwtUtil.getRefreshTokenTime()).thenReturn(3600000L);

        // Redis 관련 Mocking
        when(stringRedisTemplate.delete(eq("refreshToken:" + userId))).thenReturn(true);
        doNothing().when(hashOperations).putAll(eq("refreshToken:" + userId), anyMap());
        when(stringRedisTemplate.expire(eq("refreshToken:" + userId), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);


        // When
        TokenResponse result = authService.login(request);

        // Then
        assertNotNull(result);
        assertEquals(mockTokenResponse.getAccessToken(), result.getAccessToken());
        assertEquals(mockTokenResponse.getRefreshToken(), result.getRefreshToken());

        // 메소드 호출 검증
        verify(userRepository, times(1)).findByLoginId(request.getLoginId());
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPasswordInDb);
        verify(foundUser, times(1)).getPassword();
        verify(foundUser, atLeastOnce()).getUserId();
        verify(foundUser, atLeastOnce()).getLoginId();
        verify(foundUser, atLeastOnce()).getRole();
        verify(foundUser, atLeastOnce()).isDeleted();


        verify(jwtUtil, times(1)).createToken(userId, loginId, UserRole.valueOf("USER"));
        verify(jwtUtil, times(1)).getRefreshTokenTime();
        verify(stringRedisTemplate, times(1)).delete("refreshToken:" + userId);
        verify(hashOperations, times(1)).putAll(eq("refreshToken:" + userId), anyMap());
        verify(stringRedisTemplate, times(1)).expire(eq("refreshToken:" + userId), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 유저 찾지 못함")
    void login_UserNotFound() {
        // Given
        LoginRequestDto request = new LoginRequestDto(1L, "testLogin","1q2w3e4r!");
        when(userRepository.findByLoginId(request.getLoginId())).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> authService.login(request));
        assertEquals(ErrorCode.INFO_MISMATCH, exception.getErrorCode());

        // 메소드 호출 검증 (이후 단계는 호출되지 않음)
        verify(userRepository, times(1)).findByLoginId(request.getLoginId());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).createToken(anyLong(), anyString(), any());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    void login_PasswordMismatch() {
        // Given
        User foundUser = mock(User.class);
        LoginRequestDto request = new LoginRequestDto(foundUser.getUserId(),foundUser.getLoginId(), "wrongPassword");
        String encodedPassword = "encodedPassword";

        when(userRepository.findByLoginId(request.getLoginId())).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches(request.getPassword(), encodedPassword)).thenReturn(false);
        when(foundUser.getPassword()).thenReturn(encodedPassword);


        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> authService.login(request));
        assertEquals(ErrorCode.INFO_MISMATCH, exception.getErrorCode());

        // 메소드 호출 검증
        verify(userRepository, times(1)).findByLoginId(request.getLoginId());
        verify(passwordEncoder, times(1)).matches(request.getPassword(), encodedPassword);
        verify(jwtUtil, never()).createToken(anyLong(), anyString(), any());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 삭제된 사용자")
    void login_DeletedUser() {
        // Given
        User deletedUser = UserTestFixture.유저_생성(1L);
        LoginRequestDto request = new LoginRequestDto(deletedUser.getUserId(),deletedUser.getLoginId(), deletedUser.getPassword());
        deletedUser.deleteUser();

        when(userRepository.findByLoginId(request.getLoginId())).thenReturn(Optional.of(deletedUser));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> authService.login(request));
        assertEquals(ErrorCode.DELETED_USER, exception.getErrorCode());

        // 메소드 호출 검증
        verify(userRepository, times(1)).findByLoginId(request.getLoginId());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).createToken(anyLong(), anyString(), any());
    }

    // --- Logout Tests ---

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logout_Success() {
        // Given
        Long userId = 1L;
        String redisKey = "refreshToken:" + userId;
        when(stringRedisTemplate.delete(redisKey)).thenReturn(true);

        // When
        authService.logout(userId);

        // Then
        verify(stringRedisTemplate, times(1)).delete(redisKey);
    }

    @Test
    @DisplayName("로그아웃 실패 테스트 - Redis에 토큰이 없음 (삭제 실패)")
    void logout_TokenNotFoundInRedis() {
        // Given
        Long userId = 2L;
        String redisKey = "refreshToken:" + userId;
        when(stringRedisTemplate.delete(redisKey)).thenReturn(false);

        // When
        authService.logout(userId);

        // Then
        verify(stringRedisTemplate, times(1)).delete(redisKey);
    }
}