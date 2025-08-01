package com.example.eat_together.global.util;

import com.example.eat_together.domain.users.common.enums.UserRole;
import com.example.eat_together.global.dto.TokenResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "your-256-bit-secret-your-256-bit-secret"; // 256비트 이상 문자열
    private static final long ACCESS_TOKEN_TIME = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_TIME = 1000 * 60 * 60 * 24; // 1일
    public static final String BEARER_PREFIX = "Bearer ";
    private final Key key;

    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // 토큰 생성
    public TokenResponse createToken(Long userId, String loginId, UserRole role) {

        // Payload : 클라이언트가 서버에 요청을 보낼 시 필요한 정보들 저장
        // Jwt에 필요 정보 저장
        Claims claims = Jwts.claims();
        claims.put("userId",userId);
        claims.put("loginId",loginId);
        claims.put("role",role);

        String accessToken = BEARER_PREFIX + Jwts.builder()
                .setSubject(String.valueOf(userId)) // 보통 userId
                .claim("loginId",loginId)
                .claim("userRole",role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = BEARER_PREFIX + Jwts.builder()
                .setSubject(String.valueOf(userId)) // 보통 userId
                .claim("loginId",loginId)
                .claim("userRole",role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenResponse.of(accessToken, refreshToken);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = extractClaims(token); // 기존 extractClaims 재활용

        // "userRole" 클레임에서 역할을 가져와 "ROLE_" 접두사를 붙여 GrantedAuthority로 변환
        String roleString = claims.get("userRole", String.class); // String.class로 명시적 캐스팅
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_" + roleString));

        // UserDetails 객체 생성 (비밀번호는 필요 없으므로 빈 문자열)
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        // UsernamePasswordAuthenticationToken 반환
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 토큰에서 subject 추출
    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.");
        } catch (SecurityException | MalformedJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다.");
        } catch (UnsupportedJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT 토큰이 잘못되었습니다.");
        }
    }

    // 토큰 유효성 검사
    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Refresh Token 만료 시간
    public long getRefreshTokenTime() {
        return REFRESH_TOKEN_TIME;
    }

    public Long getUserId(String token) {
        try {
            String userId = extractClaims(token).getSubject();
            return Long.parseLong(userId);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 사용자 ID 형식입니다.");
        }
    }


}
