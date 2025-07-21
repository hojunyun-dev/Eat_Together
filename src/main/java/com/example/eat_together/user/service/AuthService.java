package com.example.eat_together.user.service;

import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.util.JwtUtil;
import com.example.eat_together.user.dto.request.LoginRequestDto;
import com.example.eat_together.user.dto.request.SignupRequestDto;
import com.example.eat_together.user.dto.response.UserResponseDto;
import com.example.eat_together.user.entity.User;
import com.example.eat_together.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public UserResponseDto signup(SignupRequestDto request) {

        // 중복된 아이디 검증
        if(userRepository.existsByLoginId(request.getLoginId())){
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }

        String encodePassword = passwordEncoder.encode(request.getPassword());

        User user = new User(request, encodePassword);
        User saveUser = userRepository.save(user);

        return new UserResponseDto(saveUser.getUserId(),
                saveUser.getLoginId(),
                saveUser.getEmail(),
                saveUser.getNickname(),
                saveUser.getRole());
    }

    public String login(LoginRequestDto request) {

        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(passwordEncoder.matches(user.getPassword(), request.getPassword())){
            throw new CustomException(ErrorCode.PASSWORD_WRONG);
        }

        return jwtUtil.createToken(user.getUserId());

    }
}
