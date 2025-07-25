package com.example.eat_together.domain.user.controller;

import com.example.eat_together.domain.user.dto.request.PasswordRequestDto;
import com.example.eat_together.domain.user.dto.request.ReissueRequestDto;
import com.example.eat_together.domain.user.dto.request.UpdateUserInfoRequestDto;
import com.example.eat_together.domain.user.dto.response.UserResponseDto;
import com.example.eat_together.domain.user.enums.MessageEnum;
import com.example.eat_together.global.dto.ApiResponse;
import com.example.eat_together.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.eat_together.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                           @Valid @RequestBody ChangePasswordRequestDto request){
        userService.changePassword(Long.valueOf(userDetails.getUsername()),request);

        return ResponseEntity.ok(ApiResponse.success(MessageEnum.CHANGE_PASSWORD.getMessage()));
    }

    // 개인 정보 수정 ( email, name, nickname )
    @PatchMapping("/update/profile")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                      @Valid @RequestBody UpdateUserInfoRequestDto request){
        log.info(String.valueOf(userDetails));
        UserResponseDto userResponseDto = userService.updateProfile(Long.valueOf(userDetails.getUsername()),request);

        return ResponseEntity.ok(ApiResponse.of(userResponseDto,MessageEnum.UPDATE_INFO.getMessage()));
    }

    // 유저 단건 조회 ( ADMIN 전용 )
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> findUser(@PathVariable Long userId){

        UserResponseDto user = userService.findUser(userId);

        return ResponseEntity.ok(ApiResponse.of(user,MessageEnum.SEARCH_INFO.getMessage()));
    }

    // 유저 전체 조회 ( ADMIN 전용 )
    @GetMapping("/find/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> findAllUsers(Pageable pageable){

        // Pageable 적용 ( page, size 값 입력해야함 )
        // ex) page = 0, size = 3 첫번째 페이지에 3명 출력
        Page<UserResponseDto> usersPage = userService.findAllUsers(pageable);

        List<UserResponseDto> allUsers = usersPage.getContent();

        return ResponseEntity.ok(ApiResponse.of(allUsers, MessageEnum.SEARCH_INFO.getMessage()));
    }

    // 마이 페이지 조회
    @GetMapping("/find/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> findMyProfile(@AuthenticationPrincipal UserDetails userDetails){

        UserResponseDto myProfile = userService.findMyProfile(Long.valueOf(userDetails.getUsername()));

        return ResponseEntity.ok(ApiResponse.of(myProfile,MessageEnum.SEARCH_INFO.getMessage()));
    }

    // 유저 삭제
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @Valid @RequestBody PasswordRequestDto request){

        userService.deleteUser(request, Long.valueOf(userDetails.getUsername()));

        return ResponseEntity.ok(ApiResponse.success(MessageEnum.DELETE_USER.getMessage()));
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> reissue(
            @Valid @RequestBody ReissueRequestDto request
    ) {

        String reissue = userService.reissue(request);

        return ResponseEntity.ok(ApiResponse.of(reissue,MessageEnum.TOKEN_REISSU.getMessage()));
    }

    // 어드민 전용 삭제된 유저 복구
    @PostMapping("/{userId}/restoration")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> restoration(@PathVariable Long userId){

        UserResponseDto restoration = userService.restoration(userId);

        return ResponseEntity.ok(ApiResponse.of(restoration,MessageEnum.USER_RESTORATION.getMessage()));
    }

    /*
    *
    * 이 API는 단순 redis 연동 확인용 API 입니다.
    *
    * */
    @GetMapping("/redis")
    public ResponseEntity<String> checkRedisConnection() {
        try {
            // Redis에 PING 명령을 보내고 PONG 응답을 확인
            // RedisTemplate은 ConnectionFactory를 통해 연결을 얻고 명령을 실행합니다.
            String response = redisTemplate.getConnectionFactory().getConnection().ping();
            if ("PONG".equals(response)) {
                return ResponseEntity.ok("Redis connection is UP.");
            } else {
                return ResponseEntity.status(500).body("Redis connection failed: " + response);
            }
        } catch (Exception e) {
            // 연결 실패 시 예외가 발생합니다.
            return ResponseEntity.status(500).body("Redis connection failed: " + e.getMessage());
        }
    }
}
