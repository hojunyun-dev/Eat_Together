package com.example.eat_together.domain.social.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KakaoOauth implements SocialOauth {

    @Value("${sns.kakao.url}")
    private String KAKAO_SNS_BASE_URL;

    @Value("${sns.kakao.client.id}")
    private String KAKAO_SNS_CLIENT_ID;

    @Value("${sns.kakao.callback.url}")
    private String KAKAO_SNS_CALLBACK_URL;

    @Value("${sns.kakao.client.secret}")
    private String KAKAO_SNS_CLIENT_SECRET; // 카카오 클라이언트 시크릿 (활성화한 경우)

    @Value("${sns.kakao.token.url}")
    private String KAKAO_SNS_TOKEN_BASE_URL;

    @Value("${sns.kakao.user-info-url}")
    private String KAKAO_SNS_USER_INFO_URL;

    @Value("${sns.kakao.scope}") // application.yml의 sns.kakao.scope를 주입받음
    private String KAKAO_SNS_SCOPE;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getOauthRedirectURL() {
        log.info("KAKAO_SNS_BASE_URL: {}", KAKAO_SNS_BASE_URL);
        log.info("KAKAO_SNS_CLIENT_ID: {}", KAKAO_SNS_CLIENT_ID);
        log.info("KAKAO_SNS_CALLBACK_URL: {}", KAKAO_SNS_CALLBACK_URL);
        log.info("KAKAO_SNS_SCOPE: {}", KAKAO_SNS_SCOPE);

        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", KAKAO_SNS_CLIENT_ID);
        params.put("redirect_uri", KAKAO_SNS_CALLBACK_URL);
        params.put("scope", KAKAO_SNS_SCOPE); // application.yml에서 주입받은 scope 사용

        String parameterString = params.entrySet().stream()
                .map(entry -> {
                    try {
                        return entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8.toString());
                    } catch (UnsupportedEncodingException e) {
                        log.error("URL 인코딩 실패", e);
                        throw new RuntimeException("URL 인코딩 실패", e);
                    }
                })
                .collect(Collectors.joining("&"));

        String finalRedirectUrl = KAKAO_SNS_BASE_URL + "?" + parameterString;
        log.info("생성된 Kakao Redirect URL: {}", finalRedirectUrl);
        return finalRedirectUrl;
    }

    @Override
    public String requestAccessToken(String code) {
        // 이 메서드는 SocialService에서 직접 호출하지 않으므로 사용하지 않거나 제거할 수 있습니다.
        throw new UnsupportedOperationException("requestAccessToken(String code) is deprecated. Use requestAccessTokenAndGetUserInfo(String code) instead.");
    }

    @Override
    public Map<String, Object> requestAccessTokenAndGetUserInfo(String code) {
        // 1. Access Token 요청
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_SNS_CLIENT_ID);
        params.add("redirect_uri", KAKAO_SNS_CALLBACK_URL);
        params.add("code", code);
        // 클라이언트 시크릿을 활성화했다면 추가
        if (KAKAO_SNS_CLIENT_SECRET != null && !KAKAO_SNS_CLIENT_SECRET.isEmpty()) {
            params.add("client_secret", KAKAO_SNS_CLIENT_SECRET);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        log.info("Kakao Access Token 요청 URL: {}", KAKAO_SNS_TOKEN_BASE_URL);
        log.info("Kakao Access Token 요청 파라미터: {}", params);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(KAKAO_SNS_TOKEN_BASE_URL, request, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.error("카카오 토큰 요청 처리 실패. 상태 코드: {}, 응답: {}", responseEntity.getStatusCode(), responseEntity.getBody());
            throw new RuntimeException("카카오 토큰 요청 처리 실패");
        }

        String accessTokenResponse = responseEntity.getBody();
        log.info("Kakao Access Token 응답: {}", accessTokenResponse);

        String accessToken;
        try {
            JsonNode rootNode = objectMapper.readTree(accessTokenResponse);
            accessToken = rootNode.get("access_token").asText();
        } catch (IOException e) {
            log.error("Kakao Access Token 파싱 실패", e);
            throw new RuntimeException("Kakao Access Token 파싱 실패", e);
        }

        // 2. 사용자 정보 요청 (Access Token 사용)
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.add("Authorization", "Bearer " + accessToken);
        userInfoHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8"); // 카카오 사용자 정보 API 요구 헤더

        HttpEntity<MultiValueMap<String, String>> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        log.info("Kakao 사용자 정보 요청 URL: {}", KAKAO_SNS_USER_INFO_URL);

        ResponseEntity<String> userInfoResponseEntity = restTemplate.postForEntity(KAKAO_SNS_USER_INFO_URL, userInfoRequest, String.class);

        if (userInfoResponseEntity.getStatusCode() != HttpStatus.OK) {
            log.error("카카오 사용자 정보 요청 처리 실패. 상태 코드: {}, 응답: {}", userInfoResponseEntity.getStatusCode(), userInfoResponseEntity.getBody());
            throw new RuntimeException("카카오 사용자 정보 요청 처리 실패");
        }

        String userInfoResponse = userInfoResponseEntity.getBody();
        log.info("Kakao 사용자 정보 응답: {}", userInfoResponse);

        Map<String, Object> userInfoMap = new HashMap<>();
        try {
            JsonNode rootNode = objectMapper.readTree(userInfoResponse);

            // 카카오 고유 ID (long 타입으로 제공됨)
            Long id = rootNode.get("id").asLong();
            userInfoMap.put("id", id.toString()); // String으로 변환하여 Map에 저장 (필요시)

            JsonNode kakaoAccount = rootNode.get("kakao_account");
            if (kakaoAccount != null) {
                // 이메일
                // 카카오는 email_needs_agreement가 true이면 동의가 필요한 상태, false이면 동의 완료 상태
                if (kakaoAccount.has("email") && !kakaoAccount.get("email_needs_agreement").asBoolean()) {
                    userInfoMap.put("email", kakaoAccount.get("email").asText());
                } else {
                    log.warn("카카오 이메일 동의 항목이 없거나 동의하지 않았습니다. 임시 이메일을 생성합니다.");
                    userInfoMap.put("email", "kakao_" + id + "@kakao.com"); // 임시 이메일 생성
                }

                JsonNode profile = kakaoAccount.get("profile");
                if (profile != null) {
                    // 닉네임
                    if (profile.has("nickname")) {
                        userInfoMap.put("nickname", profile.get("nickname").asText());
                        // Google의 'name' 필드와 일관성을 위해 'name'에도 닉네임 설정
                        userInfoMap.put("name", profile.get("nickname").asText());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Kakao 사용자 정보 파싱 실패", e);
            throw new RuntimeException("Kakao 사용자 정보 파싱 실패", e);
        }

        return userInfoMap;
    }
}