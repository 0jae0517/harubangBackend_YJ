package com.harubang.harubangBackend.controller;

import com.harubang.harubangBackend.dto.AgentSignUpDto;
import com.harubang.harubangBackend.dto.CustomerSignUpDto;
import com.harubang.harubangBackend.dto.LoginRequestDto; // [추가]
import com.harubang.harubangBackend.dto.LoginResponseDto; // [추가]
import com.harubang.harubangBackend.entity.User;
import com.harubang.harubangBackend.service.UserService;
import com.harubang.harubangBackend.util.JwtUtil; // [추가]
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil; // [추가] JwtUtil 주입

    // 고객 회원가입 API (변경 없음)
    @PostMapping("/signup/customer")
    public ResponseEntity<?> signUpCustomer(@Valid @RequestBody CustomerSignUpDto requestDto) {
        try {
            User savedUser = userService.signUpCustomer(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("고객 회원가입이 성공적으로 완료되었습니다. User ID: " + savedUser.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 중개사 회원가입 API (변경 없음)
    @PostMapping("/signup/agent")
    public ResponseEntity<?> signUpAgent(@Valid @RequestBody AgentSignUpDto requestDto) {
        try {
            User savedUser = userService.signUpAgent(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("중개사 회원가입이 성공적으로 완료되었습니다. User ID: " + savedUser.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    /**
     * [수정] 로그인 API
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto requestDto) {
        try {
            // 1. 서비스에 로그인 요청 (이메일, 비밀번호 검증)
            User user = userService.login(requestDto);

            // 2. 로그인 성공 시, JWT 토큰 생성
            String token = jwtUtil.createToken(user.getEmail(), user.getRole().name());

            // 3. 프론트엔드에 반환할 응답 DTO 생성
            LoginResponseDto responseDto = LoginResponseDto.builder()
                    .accessToken(token)
                    .userEmail(user.getEmail())
                    .userName(user.getName())
                    .userRole(user.getRole())
                    .build();

            // 4. HTTP 200 OK 상태와 함께 DTO 반환
            return ResponseEntity.ok(responseDto);

        } catch (IllegalArgumentException e) {
            // 5. 로그인 실패 시 (이메일/비밀번호 오류), HTTP 401 Unauthorized 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}