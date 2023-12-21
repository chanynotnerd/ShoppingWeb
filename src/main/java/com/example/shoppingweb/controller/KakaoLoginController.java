package com.example.shoppingweb.controller;

import com.example.shoppingweb.domain.OAuthToken;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.dto.OAuthTokenDTO;
import com.example.shoppingweb.service.KakaoLoginService;
import com.example.shoppingweb.service.OAuthTokenService;
import com.example.shoppingweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;

@Controller
public class KakaoLoginController {
    @Autowired
    private KakaoLoginService kakaoLoginService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private OAuthTokenService oAuthTokenService;

    @Value("${kakao.default.password}")
    private String kakaoPassword;


    @GetMapping("/oauth/kakao")
    public String KakaoCallback(String code) {
        String refreshToken = null;
        try {
            // 인증서버로부터 받은 CODE로 엑세스 토큰 획득
            OAuthToken oAuthToken = kakaoLoginService.getTokens(code);
            String accessToken = oAuthToken.getAccessToken();
            refreshToken = oAuthToken.getRefreshToken();

            // 엑세스 토큰을 이용하여 사용자 정보 획득
            User kakaoUser = kakaoLoginService.getUserInfo(accessToken);
            System.out.println(kakaoUser);

            // 회원인지 확인하고 해당 내용 없으면 신규 등록
            User findUser = userService.getUser(kakaoUser.getUsername());
            System.out.println("findUser is: " + findUser);
            if (findUser.getUsername() == null) {
                userService.insertUser(kakaoUser);
            }

            // 받은 사용자 정보로 인증을 처리한다.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    kakaoUser.getUsername(), kakaoPassword);

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("login with KakaoOAuth");

            // String kakaoUsername = findUser.getUsername();

            Integer userId = findUser.getId();
            System.out.println(userId);
            OAuthTokenDTO oAuthTokenDTO = new OAuthTokenDTO(accessToken, refreshToken, userId);
            oAuthTokenService.saveToken(oAuthTokenDTO);

        } catch (HttpClientErrorException e) {  // HTTP 요청 중 발생하는 예외
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // 401 에러가 난다면 엑세스 토큰이 만료된 것을 의미한다.
                // refreshOAuthToken으로 새로운 accessToken을 얻어온다.
                System.out.println("401 error incame");
                OAuthToken refreshOAuthToken = kakaoLoginService.refreshAccessToken(refreshToken);

                // 새로 발급받은 엑세스 토큰으로 사용자 정보 가져오기.
                User kakaoUser = kakaoLoginService.getUserInfo(refreshOAuthToken.getAccessToken());
                User findUser = userService.getUser(kakaoUser.getUsername());

                Integer userId = findUser.getId();

                // 토큰 정보 갱신.
                OAuthTokenDTO oAuthTokenDTO = oAuthTokenService.getToken(userId); // 복호화된 토큰 정보를 가져옴
                oAuthTokenService.saveToken(oAuthTokenDTO);
                /*OAuthTokenDTO oAuthTokenDTO = new OAuthTokenDTO(refreshOAuthToken.getAccessToken(),
                        refreshOAuthToken.getRefreshToken(),userId);
                oAuthTokenService.saveToken(oAuthTokenDTO);*/

                // 새로운 사용자 정보로 로그인 처리.
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), kakaoPassword);
                Authentication authentication = authenticationManager.authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                throw e; // 다른 에러는 무시
            }
        }
        return "redirect:/";
    }
}
