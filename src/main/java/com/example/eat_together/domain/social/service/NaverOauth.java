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
public class NaverOauth implements SocialOauth {

    @Value("${sns.naver.url}")
    private String NAVER_SNS_BASE_URL;

    @Value("${sns.naver.client.id}")
    private String NAVER_SNS_CLIENT_ID;

    @Value("${sns.naver.callback.url}")
    private String NAVER_SNS_CALLBACK_URL;

    @Value("${sns.naver.client.secret}")
    private String NAVER_SNS_CLIENT_SECRET;

    @Value("${sns.naver.token.url}")
    private String NAVER_SNS_TOKEN_BASE_URL;

    @Value("${sns.naver.user-info-url}")
    private String NAVER_SNS_USER_INFO_URL;

    // 네이버는 CSRF 방지를 위해 state 값을 필수로 요구합니다.
    @Value("${sns.naver.state}")
    private String NAVER_SNS_STATE;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getOauthRedirectURL() {
        log.info("NAVER_SNS_BASE_URL: {}", NAVER_SNS_BASE_URL);
        log.info("NAVER_SNS_CLIENT_ID: {}", NAVER_SNS_CLIENT_ID);
        log.info("NAVER_SNS_CALLBACK_URL: {}", NAVER_SNS_CALLBACK_URL);

        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", NAVER_SNS_CLIENT_ID);
        params.put("redirect_uri", NAVER_SNS_CALLBACK_URL);

        // 네이버는 CSRF 방지를 위해 state 값을 필수로 요구합니다.
        params.put("state", NAVER_SNS_STATE);

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

        String finalRedirectUrl = NAVER_SNS_BASE_URL + "?" + parameterString;
        log.info("생성된 Naver Redirect URL: {}", finalRedirectUrl);
        return finalRedirectUrl;
    }

    @Override
    public String requestAccessToken(String code) {
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
        params.add("client_id", NAVER_SNS_CLIENT_ID);
        params.add("client_secret", NAVER_SNS_CLIENT_SECRET);
        params.add("code", code);
        params.add("state", NAVER_SNS_STATE);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        log.info("Naver Access Token 요청 URL: {}", NAVER_SNS_TOKEN_BASE_URL);
        log.info("Naver Access Token 요청 파라미터: {}", params);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(NAVER_SNS_TOKEN_BASE_URL, request, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.error("네이버 토큰 요청 처리 실패. 상태 코드: {}, 응답: {}", responseEntity.getStatusCode(), responseEntity.getBody());
            throw new RuntimeException("네이버 토큰 요청 처리 실패");
        }

        String accessTokenResponse = responseEntity.getBody();
        log.info("Naver Access Token 응답: {}", accessTokenResponse);

        String accessToken;
        try {
            JsonNode rootNode = objectMapper.readTree(accessTokenResponse);
            accessToken = rootNode.get("access_token").asText();
        } catch (IOException e) {
            log.error("Naver Access Token 파싱 실패", e);
            throw new RuntimeException("Naver Access Token 파싱 실패", e);
        }

        // 2. 사용자 정보 요청 (Access Token 사용)
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.add("Authorization", "Bearer " + accessToken);
        userInfoHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        log.info("Naver 사용자 정보 요청 URL: {}", NAVER_SNS_USER_INFO_URL);

        ResponseEntity<String> userInfoResponseEntity = restTemplate.postForEntity(NAVER_SNS_USER_INFO_URL, userInfoRequest, String.class);

        if (userInfoResponseEntity.getStatusCode() != HttpStatus.OK) {
            log.error("네이버 사용자 정보 요청 처리 실패. 상태 코드: {}, 응답: {}", userInfoResponseEntity.getStatusCode(), userInfoResponseEntity.getBody());
            throw new RuntimeException("네이버 사용자 정보 요청 처리 실패");
        }

        String userInfoResponse = userInfoResponseEntity.getBody();
        log.info("Naver 사용자 정보 응답: {}", userInfoResponse);

        Map<String, Object> userInfoMap = new HashMap<>();
        try {
            JsonNode rootNode = objectMapper.readTree(userInfoResponse);
            JsonNode responseNode = rootNode.get("response");

            if (responseNode != null) {
                // 네이버 고유 ID
                if (responseNode.has("id")) {
                    userInfoMap.put("id", responseNode.get("id").asText());
                }
                // 이메일
                if (responseNode.has("email")) {
                    userInfoMap.put("email", responseNode.get("email").asText());
                } else {
                    log.warn("네이버 이메일 동의 항목이 없거나 동의하지 않았습니다. 임시 이메일을 생성합니다.");
                    userInfoMap.put("email", "naver_" + responseNode.get("id").asText() + "@naver.com"); // 임시 이메일 생성
                }
                // 닉네임
                if (responseNode.has("nickname")) {
                    userInfoMap.put("nickname", responseNode.get("nickname").asText());
                    userInfoMap.put("name", responseNode.get("nickname").asText()); // 'name' 필드에도 닉네임 설정
                } else {
                    log.warn("네이버 닉네임 동의 항목이 없거나 동의하지 않았습니다. 임시 닉네임을 생성합니다.");
                    userInfoMap.put("nickname", "네이버사용자");
                    userInfoMap.put("name", "네이버사용자");
                }
                // 이름 (실명)
                if (responseNode.has("name")) {
                    userInfoMap.put("name", responseNode.get("name").asText());
                }
                // 프로필 이미지
                if (responseNode.has("profile_image")) {
                    userInfoMap.put("profile_image_url", responseNode.get("profile_image").asText());
                }
            }
        } catch (IOException e) {
            log.error("Naver 사용자 정보 파싱 실패", e);
            throw new RuntimeException("Naver 사용자 정보 파싱 실패", e);
        }

        return userInfoMap;
    }
}