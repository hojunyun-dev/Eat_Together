package com.example.eat_together.domain.social.controller;

import com.example.eat_together.domain.social.helper.SocialLoginType;
import com.example.eat_together.domain.social.service.SocialService;
import com.example.eat_together.domain.users.common.enums.MessageEnum;
import com.example.eat_together.global.dto.ApiResponse;
import com.example.eat_together.global.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/social")
@Slf4j
public class SocialController {

    private final SocialService socialService;

    /**
     *
     * 사용자로부터 SNS 로그인 요청을 받아 해당 소셜 서비스의 인증 페이지로 리다이렉트합니다.
     *
     */
    @GetMapping("/{socialLoginType}")
    public RedirectView socialLoginType(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType){
        log.info("사용자로부터 SNS 로그인 요청을 받음 ::: {} Social Login", socialLoginType);
        String redirectUrl = socialService.request(socialLoginType);

        return new RedirectView(redirectUrl);
    }

    /**
     *
     * 소셜 로그인 API 서버로부터 인증 코드를 받아 Access Token을 요청하고 로그인 처리를 수행합니다.
     *
     */
    @GetMapping("/{socialLoginType}/callback")
    public ResponseEntity<ApiResponse<TokenResponse>> callback(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
                                                @RequestParam(name = "code") String code){
        log.info("소셜 로그인 API 서버로 받은 Code ::: {}", code);

        TokenResponse tokenResponse = socialService.requestAccessToken(socialLoginType, code);
        return ResponseEntity.ok(ApiResponse.of(tokenResponse, MessageEnum.LOGIN.getMessage()));
    }
}
