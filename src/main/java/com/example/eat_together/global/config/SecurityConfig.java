package com.example.eat_together.global.config;

import com.example.eat_together.global.util.CustomAccessDeniedHandler;
import com.example.eat_together.global.util.JwtAuthenticationEntryPoint;
import com.example.eat_together.global.util.JwtAuthenticationFilter;
import com.example.eat_together.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/signup",
                                "/auth/login",
                                "/social/**",
                                "/users/reissue",
                                "/users/redis",
                                "/favicon.ico",
                                "/ws/**",
                                "/actuator/prometheus",
                                "/chats/**")
                        .permitAll()

                        // 게스트 카트: merge는 인증 필요, 나머지 게스트 카트는 무인증
                        .requestMatchers("/guest-carts/merge").authenticated()
                        .requestMatchers("/stores/**/guest-carts/**", "/guest-carts/**").permitAll()

                        // 그 외의 요청은 @PreAuthorize를 사용하여 확인
                        .anyRequest().authenticated()
                )
                // 권한 or 유효한 토근 검증 핸들링
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패 시 호출
                        .accessDeniedHandler(customAccessDeniedHandler)) // 권한 부족 시 호출
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
