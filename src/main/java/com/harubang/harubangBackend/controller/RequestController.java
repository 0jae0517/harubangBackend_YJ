package com.harubang.harubangBackend.controller;

import com.harubang.harubangBackend.dto.ApiResponse;
import com.harubang.harubangBackend.dto.RequestCreateDto;
import com.harubang.harubangBackend.dto.RequestResponseDto;
import com.harubang.harubangBackend.entity.Request;
import com.harubang.harubangBackend.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * [신규] 고객용 내 신청서 목록 조회 API
     * GET /api/requests/my
     * (SecurityConfig에서 CUSTOMER 권한 필요)
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<RequestResponseDto>>> getMyRequests(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        String userEmail = authentication.getName();

        List<RequestResponseDto> requests = requestService.getMyRequests(userEmail);
        return ResponseEntity.ok(ApiResponse.createSuccess(requests, "내 신청서 목록 조회 성공"));
    }

    // --- [신규] 신청서 상세 조회 API ---
    /**
     * GET /api/requests/{id}
     * 특정 신청서 1건을 상세 조회합니다. (주로 중개사용)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RequestResponseDto>> getRequestById(@PathVariable Long id) {
        RequestResponseDto request = requestService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.createSuccess(request, "신청서 상세 조회 성공"));
    }

    /**
     * GET /api/requests
     * 중개사가 모든 신청서 목록을 조회합니다.
     * (SecurityConfig에서 AGENT 권한 필요)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RequestResponseDto>>> getAllRequests() {
        List<RequestResponseDto> requests = requestService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.createSuccess(requests, "전체 신청서 목록 조회 성공"));
    }
}