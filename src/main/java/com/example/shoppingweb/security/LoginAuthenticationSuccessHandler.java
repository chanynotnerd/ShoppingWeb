package com.example.shoppingweb.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;


// 로그인 인증 성공 시 수행 동작 정의 클래스, 사용자의 역할에 따라 다른 페이지로 리다이렉트 기능 구현
@Component
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 인증된 사용자의 권한(역할) 정보를 가져와 Set<String>으로 변환
        // authentication.getAuthorities()는 사용자의 권한 정보를 반환하고 Set<String>으로 변환시킴.
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // 사용자의 역할에 따라 리다이렉트 페이지 결정.
        if (roles.contains("ADMIN")) {
            response.sendRedirect("/admin");
        } else {
            response.sendRedirect("/");
        }
    }
}
