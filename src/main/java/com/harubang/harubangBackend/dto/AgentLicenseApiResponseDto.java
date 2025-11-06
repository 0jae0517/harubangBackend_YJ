package com.harubang.harubangBackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * [수정] 공공데이터 API 응답을 담기 위한 DTO
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentLicenseApiResponseDto {
    private Response response;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Body body;
        private Header header;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        private String resultCode; // "00"
        private String resultMsg;  // "NORMAL_SERVICE"
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JsonProperty("items") // "items" 배열을
        private List<Item> itemList; // "itemList" 변수에 매핑

        private int totalCount;
    }

    /**
     * [핵심 수정] 실제 중개사 정보 1건 (실제 JSON 키값과 일치시킴)
     */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("medOfficeNm") // [수정] (이전) MED_OFFICE_NM -> (실제) medOfficeNm
        private String officeName;

        @JsonProperty("estblRegNo")  // [수정] (이전) ESTBL_REG_NO -> (실제) estblRegNo
        private String registrationNumber;

        @JsonProperty("rprsvNm")      // [수정] (이전) RPRSV_NM -> (실제) rprsvNm
        private String representativeName;

        @JsonProperty("lctnRoadNmAddr") // [수정] (이전) LCTN_ROAD_NM_ADDR -> (실제) lctnRoadNmAddr
        private String roadAddress;
    }
}