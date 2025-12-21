package com.harubang.harubangBackend.controller;

import com.harubang.harubangBackend.dto.ApiResponse;
import com.harubang.harubangBackend.dto.PropertyCreateDto;
import com.harubang.harubangBackend.dto.PropertyResponseDto; // DTO 임포트
import com.harubang.harubangBackend.entity.Property;
import com.harubang.harubangBackend.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    /**
     * 중개사 매물 생성 API
     * POST /api/properties
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createProperty(
            @Valid @RequestBody PropertyCreateDto createDto,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증 정보가 없습니다. (로그인이 필요합니다)");
        }

        String userEmail = authentication.getName();
        Property savedProperty = propertyService.createProperty(createDto, userEmail);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccess(savedProperty.getId(), "매물이 성공적으로 등록되었습니다."));
    }

    /**
     * 내 매물 목록 조회 (중개사용)
     * GET /api/properties/my
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<PropertyResponseDto>>> getMyProperties( // 반환 타입 변경
                                                                                   Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String userEmail = authentication.getName();
        // DTO 리스트를 받음
        List<PropertyResponseDto> properties = propertyService.getMyProperties(userEmail);

        return ResponseEntity.ok(
                ApiResponse.createSuccess(properties, "매물 목록 조회 성공")
        );
    }

    /**
     * 매물 상세 조회
     * GET /api/properties/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyResponseDto>> getProperty(@PathVariable Long id) { // 반환 타입 변경
        // DTO를 받음
        PropertyResponseDto property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(
                ApiResponse.createSuccess(property, "매물 조회 성공")
        );
    }

    /**
     * 매물 삭제
     * DELETE /api/properties/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(
            @PathVariable Long id,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String userEmail = authentication.getName();
        propertyService.deleteProperty(id, userEmail);

        return ResponseEntity.ok(
                ApiResponse.createSuccessWithNoData("매물이 삭제되었습니다.")
        );
    }

    /**
     * 모든 매물 조회 (전체 매물 리스트)
     * GET /api/properties
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyResponseDto>>> getAllProperties() { // 반환 타입 변경
        // DTO 리스트를 받음
        List<PropertyResponseDto> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(
                ApiResponse.createSuccess(properties, "전체 매물 목록 조회 성공")
        );
    }
}