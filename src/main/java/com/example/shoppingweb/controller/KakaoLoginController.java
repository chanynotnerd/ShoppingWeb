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
            System.out.println(accessToken);
            refreshToken = oAuthToken.getRefreshToken();

            // 엑세스 토큰을 이용하여 사용자 정보 획득
            User kakaoUser = kakaoLoginService.getUserInfo(accessToken);
            System.out.println(kakaoUser);

            // 회원인지 확인하고 해당 내용 없으면 신규 등록
            User findUser = userService.getUser(kakaoUser.getUsername());
            System.out.println("findUser is: " + findUser);
            System.out.println("findUser.id is " + findUser.getId());
            if (findUser.getUsername() == null) {
                userService.insertUser(kakaoUser);
                System.out.println("InsertUser CameOut");
                System.out.println("kakaoUser after InsertUser.id is" + kakaoUser.getId());

                User findUserAfterInsertUser = userService.getUser(kakaoUser.getUsername());
                System.out.println(findUserAfterInsertUser);


                // 받은 사용자 정보로 인증을 처리한다.
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        kakaoUser.getUsername(), kakaoPassword);

                Authentication authentication = authenticationManager.authenticate(authenticationToken);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("login with KakaoOAuth");

                // String kakaoUsername = findUser.getUsername();
                // userId로 token DB에 토큰들을 저장.
                Integer userId = findUserAfterInsertUser.getId();
                System.out.println(userId);
                OAuthTokenDTO oAuthTokenDTO = new OAuthTokenDTO(accessToken, refreshToken, userId);
                oAuthTokenService.saveToken(oAuthTokenDTO);

            } else {
                // 기존 유저이면 사용자 정보로 토큰 정보 가져오기.
                // 문제점: findUser.getId()가 출력이 잘 되고 있는데도 불구하고 null이 반환되고 있음.
                // 문제점2: 회원가입이 되면서 토큰은 저장하지 않았다.

                System.out.println("findUser when already had sign up: " + findUser.getId());
                /*Integer tokentableuserid = findUser.getId();
                OAuthTokenDTO tokenFromTokenTable = oAuthTokenService.getToken(tokentableuserid);*/
                Integer getTokenUserId = findUser.getId();
                OAuthTokenDTO tokenFromTokenTable = new OAuthTokenDTO(accessToken, refreshToken, getTokenUserId);
                /*OAuthTokenDTO tokenFromTokenTable = new OAuthTokenDTO();
                tokenFromTokenTable = oAuthTokenService.getToken(getTokenUserId);*/
                System.out.println("tokenfromtokentable is: " + tokenFromTokenTable);
                // 토큰 유효성 검사
                if (!accessToken.equals(tokenFromTokenTable.getAccessToken())) {
                    // 엑세스 토큰이 유효하지 않다면, 리프레시 토큰으로 엑세스 토큰을 갱신
                    OAuthToken refreshOAuthToken = kakaoLoginService.refreshAccessToken(refreshToken);

                    // 새로 발급받은 엑세스 토큰으로 사용자 정보 가져오기.
                    User refreshkakaoUser = kakaoLoginService.getUserInfo(refreshOAuthToken.getAccessToken());
                    User refreshfindUser = userService.getUser(refreshkakaoUser.getUsername());

                    Integer userId = refreshfindUser.getId();

                    // 토큰 정보 갱신.
                    OAuthTokenDTO oAuthTokenDTO = oAuthTokenService.getToken(userId); // 복호화된 토큰 정보를 가져옴

                    // 새로 발급받은 엑세스토큰과 리프레시토큰을 저장
                    oAuthTokenDTO.setAccessToken(refreshOAuthToken.getAccessToken());
                    oAuthTokenDTO.setRefreshToken(refreshOAuthToken.getRefreshToken());

                    oAuthTokenService.saveToken(oAuthTokenDTO);

                }
                // 새로운 사용자 정보로 로그인 처리.
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), kakaoPassword);
                Authentication authentication = authenticationManager.authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("login Successfully");
            }
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
