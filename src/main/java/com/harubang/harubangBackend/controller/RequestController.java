package com.harubang.harubangBackend.controller;

import com.harubang.harubangBackend.dto.RequestCreateDto;
import com.harubang.harubangBackend.entity.Request;
import com.harubang.harubangBackend.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/requests") // 신청서 관련 API는 "/api/requests" 경로로 시작
public class RequestController {

    private final RequestService requestService;

    /**
     * 고객 신청서 생성 API
     * POST /api/requests
     * 프론트엔드에서 JWT 토큰을 "Authorization: Bearer <token>" 헤더에 담아 보내야 함
     */
    @PostMapping
    public ResponseEntity<?> createRequest(@Valid @RequestBody RequestCreateDto requestDto,
                                           Authentication authentication) {

        // Authentication 객체는 JwtAuthenticationFilter가 인증에 성공하면
        // SecurityContextHolder에 저장해 둔 인증 정보입니다.
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // authentication.getName()을 호출하면 JwtUtil에서 subject로 저장했던 "이메일"이 반환됩니다.
        String userEmail = authentication.getName();

        try {
            Request savedRequest = requestService.createRequest(requestDto, userEmail);

            // [개선] savedRequest를 그대로 반환하기보다, 응답용 DTO를 만들어 반환하는 것이 좋습니다.
            // 일단 지금은 간단하게 성공 메시지와 ID만 반환합니다.
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("신청서가 성공적으로 등록되었습니다. ID: " + savedRequest.getId());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }
}
