package com.example.eat_together.domain.user.controller;

import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


/*
*
* 프로젝트 실행 시 자동으로
* ADMIN 계정 생성
*
* */
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String adminLoginId = "admin";
        String adminName = "관리자";
        String adminPassword = passwordEncoder.encode("1234");
        String adminEmail = "admin@eatTorther.com";
        String adminnickname = "ADMIN";
        //이미 admin 계정이 있을 시 리턴
        if (userRepository.existsByLoginId(adminLoginId)) {
            return;
        }

        User adminAuth = User.createAuth(adminLoginId,adminName,adminPassword,adminEmail,adminnickname);
        adminAuth.changeRoleByAdmin();

        userRepository.save(adminAuth);
    }
}