package com.example.shoppingweb.config;

import com.example.shoppingweb.security.LoginAuthenticationSuccessHandler;
import com.example.shoppingweb.security.UserDetailsServiceImpl;
import com.example.shoppingweb.token.JwtAuthenticationFilter;
import com.example.shoppingweb.token.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Configuration
@EnableWebSecurity
public class ShoppingWebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;

    @Bean
    public SecurityContextLogoutHandler securityContextLogoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 사용자가 입력한 username으로 User 객체를 검색하고 password를 비교.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 인증 없이 접근을 허용하는 경로
        http.authorizeRequests().antMatchers("/webjars/**", "/js/**", "/image/**", "/css/**",
                "/", "/auth/**", "/item/**", "/oauth/**", "/shoppingItem/**", "/order/**", "/PayCheckSum.jsp").permitAll();

        // 이외의 경로는 인증 필요
        http.authorizeRequests().anyRequest().authenticated();

        http.formLogin().loginPage("/auth/login")   // 사용자 정의 로그인 화면 제공
                .loginProcessingUrl("/auth/securitylogin")  // 로그인 요청 URI를 변경한다.
                .successHandler(loginAuthenticationSuccessHandler); // 로그인 성공 핸들러 설정

        // CSRF 토큰을 받지 않음
        http.csrf().disable();

        // 로그아웃 설정
        http.logout().logoutUrl("/auth/logout").logoutSuccessUrl("/").deleteCookies("Authorization");

        /*        http.headers().frameOptions().sameOrigin();*/
        // JWT 필터 추가
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        // 세션 정책 변경 : 세션 사용 안함
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 헤더 설정 유지
        http.headers().frameOptions().sameOrigin();
        /*
        // 구글 로그인 설정
        http.oauth2Login().defaultSuccessUrl("/", true);*/
    }
}

