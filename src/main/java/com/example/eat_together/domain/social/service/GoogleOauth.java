package com.example.eat_together.domain.social.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class GoogleOauth implements SocialOauth {

    @Value("${sns.google.url}")
    private String GOOGLE_SNS_BASE_URL;

    @Value("${sns.google.client.id}")
    private String GOOGLE_SNS_CLIENT_ID;

    @Value("${sns.google.callback.url}")
    private String GOOGLE_SNS_CALLBACK_URL;

    @Value("${sns.google.client.secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;

    @Value("${sns.google.token.url}")
    private String GOOGLE_SNS_TOKEN_BASE_URL;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getOauthRedirectURL() {

        Map<String, Object> params = new HashMap<>();
        params.put("scope", "profile email");
        params.put("response_type", "code");
        params.put("client_id", GOOGLE_SNS_CLIENT_ID);
        params.put("redirect_uri", GOOGLE_SNS_CALLBACK_URL);

        String parameterString = params.entrySet().stream()
                .map(entry -> {
                    try {
                        return entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8.toString());
                    } catch (UnsupportedEncodingException e) {
                        log.error("URL 인코딩 실패", e); // 에러 로그 추가
                        throw new RuntimeException("URL 인코딩 실패", e);
                    }
                })
                .collect(Collectors.joining("&"));

        return GOOGLE_SNS_BASE_URL + "?" + parameterString;
    }

    /**
     * Google로부터 Access Token을 요청하고, 응답에서 ID Token을 파싱하여 사용자 정보를 Map 형태로 반환합니다.
     *
     * @param code Google 인증 서버로부터 받은 인증 코드
     * @return 파싱된 사용자 정보 (이메일, 이름, 프로필 사진 등)
     */
    public Map<String, Object> requestAccessTokenAndGetUserInfo(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", GOOGLE_SNS_CLIENT_ID);
        params.add("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        params.add("redirect_uri", GOOGLE_SNS_CALLBACK_URL);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        log.info("Google Access Token 요청 URL: {}", GOOGLE_SNS_TOKEN_BASE_URL);
        log.info("Google Access Token 요청 파라미터: {}", params);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_SNS_TOKEN_BASE_URL, request, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();
            log.info("Google Access Token 요청 성공. 응답: {}", responseBody);

            try {
                JsonNode rootNode = objectMapper.readTree(responseBody);
                String idToken = rootNode.get("id_token").asText();

                String[] chunks = idToken.split("\\.");
                if (chunks.length < 2) {
                    throw new IllegalArgumentException("Invalid ID Token format: " + idToken);
                }
                Base64.Decoder decoder = Base64.getUrlDecoder();

                String payload = new String(decoder.decode(chunks[1]), StandardCharsets.UTF_8);
                log.info("ID Token 페이로드: {}", payload);

                return objectMapper.readValue(payload, Map.class);

            } catch (IOException e) {
                log.error("Google Access Token 응답 또는 ID Token 파싱 실패", e);
                throw new RuntimeException("Google Access Token 응답 또는 ID Token 파싱 실패", e);
            }
        }
        log.error("구글 로그인 토큰 요청 처리 실패. 상태 코드: {}, 응답: {}", responseEntity.getStatusCode(), responseEntity.getBody());
        throw new RuntimeException("구글 로그인 토큰 요청 처리 실패");
    }

    @Override
    public String requestAccessToken(String code) {
        throw new UnsupportedOperationException("requestAccessToken(String code) is deprecated. Use requestAccessTokenAndGetUserInfo(String code) instead.");
    }
}