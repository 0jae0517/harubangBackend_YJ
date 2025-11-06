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

    private final RestTemplate restTemplate; // AppConfig에 등록된 Bean 주입

    @Value("${open.api.serviceKey}")
    private String serviceKey;

    @Value("${open.api.baseUrl}")
    private String baseUrl;

    /**
     * (기존 코드) 오픈 API를 호출하여 중개사 자격 정보를 조회합니다. (최종 검증용)
     * @param registrationNumber 사용자가 입력한 개설등록번호
     * @return API 응답에서 파싱된 중개사 정보 (Item 객체)
     */
    public AgentLicenseApiResponseDto.Item getAgentInfo(String registrationNumber) {

        // 1. API 요청 URI 생성 (명세서의 파라미터명 사용)
        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("ESTBL_REG_NO", registrationNumber) // "개설등록번호" 파라미터
                .queryParam("numOfRows", "1") // 1개만 조회
                .queryParam("pageNo", "1")
                .queryParam("type", "json") // 응답 타입을 JSON으로 요청
                .build() // [수정] 1. 먼저 빌드
                .encode() // [수정] 2. 그 다음 인코딩
                .toUri(); // [수정] 3. URI로 변환

        System.out.println("Request URI: " + uri); // (디버깅용 로그)

        try {
            // 2. RestTemplate으로 API 호출 (GET 요청)
            AgentLicenseApiResponseDto response = restTemplate.getForObject(uri, AgentLicenseApiResponseDto.class);

            // 3. 응답 파싱 (DTO 구조에 맞게 수정)
            if (response != null && response.getResponse() != null &&
                    response.getResponse().getBody() != null &&
                    response.getResponse().getBody().getTotalCount() > 0 && // 조회 결과가 1건 이상이고
                    response.getResponse().getBody().getItems() != null &&
                    !response.getResponse().getBody().getItems().getItem().isEmpty()) { // item 리스트가 비어있지 않다면

                // 조회된 첫 번째 아이템 반환
                return response.getResponse().getBody().getItems().getItem().get(0);
            } else {
                // API에서 조회 결과가 없는 경우
                return null;
            }
        } catch (Exception e) {
            // API 호출 실패 시
            e.printStackTrace(); // (디버깅용 로그)
            throw new RuntimeException("공인중개사 정보 조회에 실패했습니다.");
        }
    }

    /**
     * [새로 추가]
     * 중개사무소 상호명 또는 개설등록번호로 목록을 검색합니다. (실시간 검색용)
     * @param query 프론트엔드에서 넘어온 검색어
     * @return 검색된 중개사무소 목록
     */
    public List<AgentLicenseApiResponseDto.Item> searchAgents(String query) {
        // 검색어가 비어있으면 빈 목록 반환
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 1. URI 빌더 생성
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", "10") // 검색 결과는 최대 10개만
                .queryParam("pageNo", "1")
                .queryParam("type", "json"); // JSON 타입으로 요청

        // 2. 검색어가 숫자인지 문자인지 판별
        boolean isRegistrationNumber = query.contains("-") || query.matches("\\d+");

        if (isRegistrationNumber) {
            uriBuilder.queryParam("ESTBL_REG_NO", query); // 개설등록번호로 검색
        } else {
            uriBuilder.queryParam("MED_OFFICE_NM", query); // 상호명으로 검색
        }

        // [수정] .build().encode().toUri() 순서로 변경
        URI uri = uriBuilder
                .build() // [수정] 1. 먼저 빌드
                .encode() // [수정] 2. 그 다음 인코딩
                .toUri(); // [수정] 3. URI로 변환

        System.out.println("Search URI: " + uri); // 디버깅 로그

        try {
            // 3. API 호출
            AgentLicenseApiResponseDto response = restTemplate.getForObject(uri, AgentLicenseApiResponseDto.class);

            // 4. 응답 파싱
            if (response != null && response.getResponse() != null &&
                    response.getResponse().getBody() != null &&
                    response.getResponse().getBody().getTotalCount() > 0 &&
                    response.getResponse().getBody().getItems() != null &&
                    !response.getResponse().getBody().getItems().getItem().isEmpty()) {

                // 조회된 아이템 리스트 반환
                return response.getResponse().getBody().getItems().getItem();
            } else {
                return Collections.emptyList(); // 결과가 없으면 빈 목록 반환
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // 오류 발생 시 빈 목록 반환
        }
    }
}