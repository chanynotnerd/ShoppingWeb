package com.example.shoppingweb.controller;

import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.service.KakaoLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class KakaoLoginController {
    @Autowired
    private KakaoLoginService kakaoLoginService;

    @GetMapping("/oauth/kakao")
    public @ResponseBody User KakaoCallback(String code) {
        // 인증서버로부터 받은 CODE로 엑세스 토큰 획득
        String accessToken = kakaoLoginService.getAccessToken(code);

        User userInfo = kakaoLoginService.getUserInfo(accessToken);
        return userInfo;
    }
}
