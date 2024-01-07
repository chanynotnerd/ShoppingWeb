package com.example.shoppingweb.token;

import com.example.shoppingweb.domain.User;
import com.example.shoppingweb.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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
    private final long exp = 1000L * 5;   // 만료시간: 5초
    private SecretKey secretKey;
    @Value("${jwt.secret.key}")
    private String chanySecretKey;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(chanySecretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰 생성
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

    // 권한정보 획득
    // Spring Security 인증과정에서 권한확인을 위한 기능
    public Authentication getAuthentication(String token) {
        // 토큰에서 사용자 이름을 추출
        String username = this.getAccount(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getAccount(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에 담겨있는 유저 account 획득
    public String getAccount(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    // Authorization Header를 통해 인증을 한다.
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            // Bearer 검증
            if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
                return false;
            } else {
                token = token.split(" ")[1].trim();
            }
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            // 만료되었을 시 false
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
