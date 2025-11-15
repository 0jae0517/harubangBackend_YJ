package com.harubang.harubangBackend.filter;

import com.harubang.harubangBackend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // ⭐ 인증이 필요없는 경로는 필터를 건너뜀
        String requestPath = request.getRequestURI();
        if (requestPath.startsWith("/api/auth/") ||
                requestPath.equals("/error") ||
                requestPath.startsWith("/api/agent-licenses/search")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. "Authorization" 헤더에서 토큰 추출
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        final String userRole;

        // 2. 헤더가 없거나 "Bearer "로 시작하지 않으면 필터 통과
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 접두사 제거
        jwt = authHeader.substring(7);

        try {
            // 4. 토큰에서 사용자 이메일(username) 추출
            userEmail = jwtUtil.extractUsername(jwt);

            // 5. 토큰 유효성 검사 및 SecurityContext에 인증 정보 저장
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtil.validateToken(jwt)) {
                    // 토큰에서 역할(role) 정보 추출
                    userRole = jwtUtil.extractClaim(jwt, claims -> claims.get("role", String.class));

                    // 역할 정보를 기반으로 Authority 생성
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userRole);

                    // 인증 토큰 생성
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userEmail,
                            null,
                            Collections.singletonList(authority)
                    );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 6. SecurityContextHolder에 인증 정보 등록
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // 토큰 파싱 실패 시 로그만 남기고 필터 체인 계속 진행
            System.err.println("JWT 파싱 실패: " + e.getMessage());
        }

        // 7. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}