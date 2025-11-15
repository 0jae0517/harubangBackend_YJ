package com.harubang.harubangBackend.config;

import com.harubang.harubangBackend.exception.CustomAccessDeniedHandler;
import com.harubang.harubangBackend.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 모든 출처 허용 (개발 단계)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // 또는 특정 도메인만 허용하려면:
        // configuration.setAllowedOrigins(Arrays.asList(
        //     "http://localhost:5173",
        //     "http://localhost:3000",
        //     "https://your-production-domain.com"
        // ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // preflight 결과 캐싱 시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용 시 필요)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션 사용 안 함 (JWT 기반)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 예외 처리
                .exceptionHandling(ex ->
                        ex.accessDeniedHandler(customAccessDeniedHandler)
                )

                // 경로별 권한 설정 (순서가 중요합니다! 더 구체적인 경로를 먼저!)
                .authorizeHttpRequests(authz -> authz
                        // 인증 없이 접근 가능한 경로들
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // Preflight 요청 허용
                        .requestMatchers("/api/auth/**").permitAll()  // 회원가입, 로그인
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/agent-licenses/search").permitAll()

                        // 매물 관련 권한 설정
                        .requestMatchers(HttpMethod.POST, "/api/properties").hasRole("AGENT")
                        .requestMatchers(HttpMethod.GET, "/api/properties/my").hasRole("AGENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/properties/**").hasRole("AGENT")
                        .requestMatchers(HttpMethod.GET, "/api/properties/**").permitAll()  // 매물 상세/목록은 누구나
                        .requestMatchers(HttpMethod.GET, "/api/properties").permitAll()  // 전체 매물 목록

                        // 신청서 관련 권한 설정
                        .requestMatchers(HttpMethod.POST, "/api/requests").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/requests/my").hasRole("CUSTOMER")

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}