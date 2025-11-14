package com.harubang.harubangBackend.controller;

import com.harubang.harubangBackend.dto.ApiResponse; // [추가]
import com.harubang.harubangBackend.dto.RequestCreateDto;
import com.harubang.harubangBackend.entity.Request;
import com.harubang.harubangBackend.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.AccessDeniedException; // [삭제] (Handler가 처리)
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;

    /**
     * [수정] 고객 신청서 생성 API (try-catch 삭제, ApiResponse 반환)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createRequest(@Valid @RequestBody RequestCreateDto requestDto,
                                                           Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            // 이 부분은 Filter에서 처리되지만, 만약을 위한 방어 코드
            // GlobalExceptionHandler가 AccessDeniedException을 처리해줌
            throw new IllegalStateException("인증 정보가 없습니다. (로그인이 필요합니다)");
        }

        String userEmail = authentication.getName();

        // 서비스 호출 (실패 시 Service가 throw한 예외를 Handler가 잡음)
        Request savedRequest = requestService.createRequest(requestDto, userEmail);

        // 성공 시 201 Created 응답 (data로 생성된 ID 반환)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccess(savedRequest.getId(), "신청서가 성공적으로 등록되었습니다."));
    }
}