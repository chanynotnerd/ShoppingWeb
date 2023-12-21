package com.example.shoppingweb.service;

import com.example.shoppingweb.domain.OAuthToken;
import com.example.shoppingweb.domain.RoleType;
import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.dto.OAuthType;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KakaoLoginService {
    @Value("${kakao.default.password}")
    private String kakaoPassword;


    public OAuthToken getTokens(String code) {
        // HttpHeaders 생성(MIME 종류)
        HttpHeaders header = new HttpHeaders();
        header.add("content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 생성(4개의 필수 매개변수 설정)
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "7726cc35fb60a4fcf57862d725f173de");
        body.add("redirect_uri", "http://localhost:8080/oauth/kakao");
        body.add("code", code);

        // HttpHeaders와 HttpBody가 설정된 HttpEntity 객체 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, header);

        // RestTemplate을 이용하면 브라우저 없이 HTTP 요청을 처리할 수 있다.
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 요청 및 응답 받기
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token", // 엑세스 토큰 요청 주소
                HttpMethod.POST, // 요청 방식
                requestEntity, // 요청 헤더와 바디
                String.class // 응답받을 타입
        );

        // HTTP 응답 본문(body) 정보 반환
        String jsonData = responseEntity.getBody();

        // JSON 데이터에서 엑세스토큰 정보 추출
        Gson gsonobj = new Gson();
        Map<?, ?> data = gsonobj.fromJson(jsonData, Map.class);

        OAuthToken oAuthToken = new OAuthToken();
        oAuthToken.setAccessToken((String) data.get("access_token"));
        oAuthToken.setRefreshToken((String) data.get("refresh_token"));

        return oAuthToken;
    }

    public OAuthToken refreshAccessToken(String refreshToken) {
        HttpHeaders refreshHeader = new HttpHeaders();
        refreshHeader.add("content-type", "application/x-www-form-urlencoded;charset=utf-8");

        LinkedMultiValueMap<String, String> refreshBody = new LinkedMultiValueMap<>();

        refreshBody.add("grant_type", "refresh_token");
        refreshBody.add("client_id", "7726cc35fb60a4fcf57862d725f173de");
        refreshBody.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> requestRefreshEntity = new HttpEntity<>(refreshBody, refreshHeader);

        // RestTemplate을 이용하면 브라우저 없이 HTTP 요청을 처리할 수 있다.
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 요청 및 응답 받기
        ResponseEntity<String> responseRefreshEntity = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token", // 엑세스 토큰 요청 주소
                HttpMethod.POST, // 요청 방식
                requestRefreshEntity, // 요청 헤더와 바디
                String.class // 응답받을 타입
        );
        // HTTP 응답 본문(body) 정보 반환
        String jsonData = responseRefreshEntity.getBody();

        // JSON 데이터에서 엑세스토큰 정보 추출
        Gson gsonobj = new Gson();
        Map<?, ?> data = gsonobj.fromJson(jsonData, Map.class);

        OAuthToken oAuthToken = new OAuthToken();
        oAuthToken.setAccessToken((String) data.get("access_token"));
        oAuthToken.setRefreshToken((String) data.get("refresh_token"));

        return oAuthToken;
    }

    public User getUserInfo(String accessToken) {
        // HttpHeader 생성
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Bearer " + accessToken);
        header.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpHeader와 HttpBody를 하나의 객체에 담기, header는 생략 가능
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(header);

        // RestTemplate을 이용하면 브라우저 없이 HTTP 요청을 처리할 수 있다.
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 요청을 POST(GET) 방식으로 실행 -> 문자열이 응답으로 들어온다.
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        // 카카오 인증 서버가 반환한 사용자 정보
        String userInfo = responseEntity.getBody();

        // JSON	데이터에서 추출한 정보로 USER 객체 설정
        Gson gsonObj = new Gson();
        Map<?, ?> data = gsonObj.fromJson(userInfo, Map.class);

        Double id = (Double) (data.get("id"));
        String nickname =
                (String) ((Map<?, ?>) (data.get("properties"))).get("nickname");
        String email =
                (String) ((Map<?, ?>) (data.get("kakao_account"))).get("email");

        User user = new User();
        // id는 자동증가값이기 때문에 따로 set해주지 않음.
        user.setUsername(email);
        user.setPassword(kakaoPassword);
        user.setEmail(email);
        user.setRole(RoleType.USER);
        user.setOauth(OAuthType.KAKAO);
        return user;
    }
}
