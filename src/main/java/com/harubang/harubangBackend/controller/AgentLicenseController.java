package com.harubang.harubangBackend.controller;

import com.harubang.harubangBackend.dto.AgentLicenseApiResponseDto;
import com.harubang.harubangBackend.service.AgentLicenseApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/agent-licenses") // 중개사 자격증 관련 API
public class AgentLicenseController {

    private final AgentLicenseApiService agentLicenseApiService;

    /**
     * 중개사무소 검색 API (회원가입 전용)
     * GET /api/agent-licenses/search?query=...
     */
    @GetMapping("/search")
    public ResponseEntity<List<AgentLicenseApiResponseDto.Item>> searchAgent(
            @RequestParam("query") String query) {

        List<AgentLicenseApiResponseDto.Item> results = agentLicenseApiService.searchAgents(query);
        return ResponseEntity.ok(results);
    }
}