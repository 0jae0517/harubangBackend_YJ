package com.harubang.harubangBackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 공공데이터 API 응답을 담기 위한 DTO
 * (JSON/XML 응답이 <response><body><items><item>...</item></items></body></response> 구조라고 가정)
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // 모르는 필드는 무시
public class AgentLicenseApiResponseDto {
    // 공공데이터 API는 응답이 XML/JSON 모두 <response> 태그로 감싸져 있는 경우가 많습니다.
    private Response response;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Body body;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private Items items;
        private int totalCount; // 총 결과 수
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        // API가 item을 리스트로 반환하는지, 단일 객체로 반환하는지에 따라 List<Item> 또는 Item
        // 검색(search)은 보통 List로 반환됩니다.
        private List<Item> item;
    }

    /**
     * [핵심] 실제 중개사 정보 1건 (알려주신 항목명과 일치시킴)
     */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("MED_OFFICE_NM") // JSON의 "MED_OFFICE_NM" 필드를
        private String officeName;      // officeName 변수에 매핑

        @JsonProperty("ESTBL_REG_NO")  // JSON의 "ESTBL_REG_NO" 필드를
        private String registrationNumber; // registrationNumber 변수에 매핑

        @JsonProperty("RPRSV_NM")      // JSON의 "RPRSV_NM" 필드를
        private String representativeName; // representativeName 변수에 매핑

        @JsonProperty("LCTN_ROAD_NM_ADDR")
        private String roadAddress; // 소재지도로명주소

        // ... (필요한 다른 필드들도 @JsonProperty로 추가 가능)
    }
}