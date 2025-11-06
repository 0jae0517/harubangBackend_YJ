package com.harubang.harubangBackend.service;

import com.harubang.harubangBackend.dto.AgentLicenseApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentLicenseApiService {

    private final RestTemplate restTemplate;

    @Value("${open.api.serviceKey}")
    private String serviceKey;

    @Value("${open.api.baseUrl}")
    private String baseUrl;

    /**
     * (최종 검증용) 개설등록번호로 중개사 자격 정보 1건 조회
     */
    public AgentLicenseApiResponseDto.Item getAgentInfo(String registrationNumber) {

        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("serviceKey", serviceKey)
                // [수정] 요청 파라미터도 카멜 케이스(estblRegNo)로 변경
                .queryParam("estblRegNo", registrationNumber)
                .queryParam("numOfRows", "1")
                .queryParam("pageNo", "1")
                .queryParam("type", "json")
                .encode()
                .build()
                .toUri();

        System.out.println("Request URI (getAgentInfo): " + uri);

        try {
            AgentLicenseApiResponseDto response = restTemplate.getForObject(uri, AgentLicenseApiResponseDto.class);

            // [수정] DTO 구조 변경에 따른 파싱 로직 수정
            if (response != null && response.getResponse() != null &&
                    response.getResponse().getHeader() != null && "00".equals(response.getResponse().getHeader().getResultCode()) &&
                    response.getResponse().getBody() != null &&
                    response.getResponse().getBody().getTotalCount() > 0 &&
                    response.getResponse().getBody().getItemList() != null &&
                    !response.getResponse().getBody().getItemList().isEmpty()) {

                return response.getResponse().getBody().getItemList().get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("공인중개사 정보 조회에 실패했습니다.");
        }
    }

    /**
     * [수정] (실시간 검색용) 상호명 또는 개설등록번호로 목록 검색
     */
    public List<AgentLicenseApiResponseDto.Item> searchAgents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", "10")
                .queryParam("pageNo", "1")
                .queryParam("type", "json");

        boolean isRegistrationNumber = query.contains("-") || query.matches("\\d+");

        if (isRegistrationNumber) {
            // [수정] 요청 파라미터도 카멜 케이스(estblRegNo)로 변경
            uriBuilder.queryParam("estblRegNo", query);
        } else {
            // [수정] 요청 파라미터도 카멜 케이스(medOfficeNm)로 변경
            uriBuilder.queryParam("medOfficeNm", query);
        }

        URI uri = uriBuilder.encode().build().toUri();
        System.out.println("Search URI: " + uri);

        try {
            AgentLicenseApiResponseDto response = restTemplate.getForObject(uri, AgentLicenseApiResponseDto.class);

            // [수정] DTO 구조 변경에 따른 파싱 로직 수정
            if (response != null && response.getResponse() != null &&
                    response.getResponse().getHeader() != null && "00".equals(response.getResponse().getHeader().getResultCode()) &&
                    response.getResponse().getBody() != null &&
                    response.getResponse().getBody().getTotalCount() > 0 &&
                    response.getResponse().getBody().getItemList() != null &&
                    !response.getResponse().getBody().getItemList().isEmpty()) {

                return response.getResponse().getBody().getItemList();
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}