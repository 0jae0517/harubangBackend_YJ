package com.harubang.harubangBackend.exception;

import com.harubang.harubangBackend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 모든 @RestController에서 발생하는 예외를 가로챔
public class GlobalExceptionHandler {

    // 1. @Valid 유효성 검사 실패 시 (예: @NotBlank, @Email)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 유효성 검사 실패 시 첫 번째 에러 메시지를 가져옴
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        ApiResponse<Void> response = ApiResponse.createError(errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }

    // 2. 비즈니스 로직 상의 예외 (예: "이미 가입된 이메일입니다.")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<Void> response = ApiResponse.createError(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }

    // 3. 권한 없음 예외 (예: 고객이 중개사 API 호출 시)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponse<Void> response = ApiResponse.createError("접근 권한이 없습니다.");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); // 403
    }

    // 4. API 인증 실패 예외 (예: 토큰이 없거나 만료됨 - SecurityConfig에서 처리하지만, 혹시 모를 경우)
    // (Spring Security의 AuthenticationException을 처리할 수 있음)
    // ... (필요시 추가) ...

    // 5. 기타 모든 서버 내부 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
        // [중요] 실제 운영 시에는 로그를 반드시 남겨야 합니다.
        // log.error("Internal Server Error: ", ex);
        ex.printStackTrace(); // (개발용)

        ApiResponse<Void> response = ApiResponse.createError("서버 내부 오류가 발생했습니다.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }
}