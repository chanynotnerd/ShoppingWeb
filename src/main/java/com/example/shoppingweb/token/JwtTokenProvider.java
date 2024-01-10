package com.example.shoppingweb.token;

import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final long exp = 1000L * 30;   // 만료시간: 30초
    private final long refreshTokenExp = 1000L * 60 * 60 * 24 * 7; // 만료시간: 1주일
    private SecretKey secretKey;
    @Value("${jwt.secret.key}")
    private String chanySecretKey;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(chanySecretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 엑세스토큰 생성
    public String createToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername()); // 사용자 이름 설정
        claims.put("email", user.getEmail()); // 이메일을 클레임에 추가
        claims.put("role", user.getRole().toString()); // 역할을 클레임에 추가
        claims.put("authority", user.getAuthority().getAuthorityName()); // 권한 이름을 클레임에 추가

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 위에서 설정한 클레임 사용
                .setIssuedAt(now) // 현재 시간을 발행 시간으로 설정
                .setExpiration(new Date(now.getTime() + exp)) // 만료 시간 설정
                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명 알고리즘과 키 사용
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExp)) // 리프레시 토큰 만료 시간 설정
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 권한정보 획득
    // Spring Security 인증과정에서 권한확인을 위한 기능
    public Authentication getAuthentication(String token) {
        /*// 토큰에서 사용자 이름을 추출
        String username = this.getAccount(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getAccount(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());*/
        String username = this.getAccount(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        logger.info("User details loaded for user: " + username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

    }

    // 토큰에 담겨있는 유저 account 획득
    public String getAccount(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    // Authorization Header를 통해 인증을 한다.
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            if (!token.toLowerCase().startsWith("bearer ")) {
                return false;
            }
            token = token.substring(7);
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            // 만료되었을 시 false
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
