package com.harubang.harubangBackend.controller;

import com.harubang.harubangBackend.dto.ApiResponse; // [추가]
import com.harubang.harubangBackend.dto.PropertyCreateDto;
import com.harubang.harubangBackend.entity.Property;
import com.harubang.harubangBackend.service.PropertyService;
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
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    /**
     * [수정] 중개사 매물 생성 API (try-catch 삭제, ApiResponse 반환)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createProperty(@Valid @RequestBody PropertyCreateDto createDto,
                                                            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증 정보가 없습니다. (로그인이 필요합니다)");
        }

        String userEmail = authentication.getName(); // 중개사 이메일

        // 서비스 호출 (실패 시 Service가 throw한 예외를 Handler가 잡음)
        Property savedProperty = propertyService.createProperty(createDto, userEmail);

        // 성공 시 201 Created 응답 (data로 생성된 ID 반환)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccess(savedProperty.getId(), "매물이 성공적으로 등록되었습니다."));
    }
}