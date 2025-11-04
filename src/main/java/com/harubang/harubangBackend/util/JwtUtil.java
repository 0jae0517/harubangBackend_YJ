package com.harubang.harubangBackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails; // 임시로 UserDetails 사용 (나중에 CustomUserDetails로 변경)
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component // Spring Bean으로 등록
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMs;

    // application.properties에서 값 주입
    public JwtUtil(@Value("${jwt.secret.key}") String secret,
                   @Value("${jwt.expiration.ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    // 1. 토큰에서 사용자 이메일(username) 추출
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2. 토큰에서 만료 시간 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 3. 토큰에서 특정 Claim 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 4. 토큰에서 모든 Claims 추출
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 5. 토큰이 만료되었는지 확인
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 6. 토큰 생성 (사용자 이메일, 역할 정보 포함)
    public String createToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // "role"이라는 이름으로 역할 정보(CUSTOM, AGENT 등) 추가
        return createToken(claims, username);
    }

    // 7. 토큰 생성 (내부 로직)
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date(System.currentTimeMillis());
        Date expirationDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claims(claims) // 추가 정보 (예: 역할)
                .subject(subject) // 토큰 주체 (예: 이메일)
                .issuedAt(now) // 발급 시간
                .expiration(expirationDate) // 만료 시간
                .signWith(secretKey, Jwts.SIG.HS256) // 서명 (비밀키, 알고리즘)
                .compact();
    }

    // 8. 토큰 유효성 검사 (사용자 이름 일치 + 만료 여부)
    // (임시로 UserDetails 사용)
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 9. (필터용) 토큰 유효성 검사 (만료 여부만 체크)
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            // 토큰 파싱 실패 (서명 불일치, 만료 등)
            return false;
        }
    }
}