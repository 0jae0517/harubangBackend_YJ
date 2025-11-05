package com.harubang.harubangBackend.controller;

import com.harubang.harubangBackend.dto.PropertyCreateDto;
import com.harubang.harubangBackend.entity.Property;
import com.harubang.harubangBackend.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/properties") // 매물 관련 API는 "/api/properties" 경로로 시작
public class PropertyController {

    private final PropertyService propertyService;

    /**
     * 중개사 매물 생성 API
     * POST /api/properties
     * AGENT 역할 사용자만 호출 가능
     */
    @PostMapping
    public ResponseEntity<?> createProperty(@Valid @RequestBody PropertyCreateDto createDto,
                                            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String userEmail = authentication.getName(); // 중개사 이메일

        try {
            Property savedProperty = propertyService.createProperty(createDto, userEmail);

            // [개선] savedProperty를 그대로 반환하기보다, 응답용 DTO를 만들어 반환하는 것이 좋습니다.
            // 일단 지금은 간단하게 성공 메시지와 ID만 반환합니다.
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("매물이 성공적으로 등록되었습니다. ID: " + savedProperty.getId());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }
}