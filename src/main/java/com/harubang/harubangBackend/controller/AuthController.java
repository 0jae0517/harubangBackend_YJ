package com.harubang.harubangBackend.controller;

import com.harubang.harubangBackend.dto.AgentSignUpDto;
import com.harubang.harubangBackend.dto.ApiResponse; // [추가]
import com.harubang.harubangBackend.dto.CustomerSignUpDto;
import com.harubang.harubangBackend.dto.LoginRequestDto;
import com.harubang.harubangBackend.dto.LoginResponseDto; // [추가]
import com.harubang.harubangBackend.entity.User;
import com.harubang.harubangBackend.service.UserService;
import com.harubang.harubangBackend.util.JwtUtil;
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
    private final JwtUtil jwtUtil;

    /**
     * [수정] 고객 회원가입 API (try-catch 삭제, ApiResponse 반환)
     */
    @PostMapping("/signup/customer")
    public ResponseEntity<ApiResponse<Void>> signUpCustomer(@Valid @RequestBody CustomerSignUpDto requestDto) {

        userService.signUpCustomer(requestDto); // 서비스 호출 (예외는 GlobalExceptionHandler가 처리)

        // 성공 시 201 Created 응답 (데이터 없음)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccessWithNoData("고객 회원가입이 성공적으로 완료되었습니다."));
    }

    /**
     * [수정] 중개사 회원가입 API (try-catch 삭제, ApiResponse 반환)
     */
    @PostMapping("/signup/agent")
    public ResponseEntity<ApiResponse<Void>> signUpAgent(@Valid @RequestBody AgentSignUpDto requestDto) {

        userService.signUpAgent(requestDto); // 서비스 호출 (예외는 GlobalExceptionHandler가 처리)

        // 성공 시 201 Created 응답 (데이터 없음)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccessWithNoData("중개사 회원가입이 성공적으로 완료되었습니다."));
    }

    /**
     * [수정] 로그인 API (try-catch 삭제, ApiResponse 반환)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto requestDto) {

        // 1. 서비스에 로그인 요청 (실패 시 Service가 throw한 예외를 Handler가 잡음)
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

        // 4. HTTP 200 OK 상태와 함께 DTO를 data 필드에 담아 반환
        return ResponseEntity.ok(ApiResponse.createSuccess(responseDto, "로그인 성공"));
    }
}