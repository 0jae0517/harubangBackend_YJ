package com.harubang.harubangBackend.controller;

import com.harubang.harubangBackend.dto.ApiResponse;
import com.harubang.harubangBackend.dto.ProposalCreateDto;
import com.harubang.harubangBackend.dto.ProposalResponseDto; // 1. DTO 임포트
import com.harubang.harubangBackend.entity.Proposal;
import com.harubang.harubangBackend.service.ProposalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*; // 2. GetMapping, PathVariable 임포트

import java.util.List; // 3. List 임포트

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/proposals")
public class ProposalController {

    private final ProposalService proposalService;

    /**
     * 매물 제안 생성 API
     * POST /api/proposals
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createProposal(
            @Valid @RequestBody ProposalCreateDto createDto,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증 정보가 없습니다. (로그인이 필요합니다)");
        }
        String agentEmail = authentication.getName();

        Proposal savedProposal = proposalService.createProposal(createDto, agentEmail);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccess(savedProposal.getId(), "매물 제안이 성공적으로 등록되었습니다."));
    }

    // --- [신규] 고객용 제안 목록 조회 API ---
    /**
     * GET /api/proposals/request/{requestId}
     * 고객이 특정 신청서에 대해 받은 제안 목록을 조회합니다.
     */
    @GetMapping("/request/{requestId}")
    public ResponseEntity<ApiResponse<List<ProposalResponseDto>>> getProposalsForRequest(
            @PathVariable("requestId") Long requestId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        String customerEmail = authentication.getName();

        List<ProposalResponseDto> proposals = proposalService.getProposalsByRequest(requestId, customerEmail);
        return ResponseEntity.ok(ApiResponse.createSuccess(proposals, "신청서별 제안 목록 조회 성공"));
    }
}