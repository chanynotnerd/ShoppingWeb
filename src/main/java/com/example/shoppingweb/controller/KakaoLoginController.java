package com.example.shoppingweb.controller;

import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.service.KakaoLoginService;
import com.example.shoppingweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KakaoLoginController {
    @Autowired
    private KakaoLoginService kakaoLoginService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Value("${kakao.default.password}")
    private String kakaoPassword;

    @GetMapping("/oauth/kakao")
    public String KakaoCallback(String code) {
        // 인증서버로부터 받은 CODE로 엑세스 토큰 획득
        String accessToken = kakaoLoginService.getAccessToken(code);

        // 엑세스 토큰을 이용하여 사용자 정보 획득
        User kakaoUser = kakaoLoginService.getUserInfo(accessToken);
        // 회원인지 확인하고 해당 내용 없으면 신규 등록
        User findUser = userService.getUser(kakaoUser.getUsername());
        if (findUser.getUsername() == null) {
            userService.insertUser(kakaoUser);
        }

        // 받은 사용자 정보로 인증을 처리한다.
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                kakaoUser.getUsername(), kakaoPassword);

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/";
    }
}
