package com.harubang.harubangBackend.dto;

import com.harubang.harubangBackend.entity.Request;
import com.harubang.harubangBackend.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// 고객이 신청서 작성 시 보낼 데이터를 받는 객체
@Getter
@Setter
public class RequestCreateDto {

    @NotBlank(message = "희망 매물 종류는 필수입니다.")
    private String propertyType; // "아파트", "오피스텔" 등

    @NotBlank(message = "희망 거래 유형은 필수입니다.")
    private String transactionType; // "전세", "월세", "매매"

    @NotBlank(message = "희망 지역은 필수입니다.")
    private String location;

    @NotBlank(message = "희망 보증금/매매가는 필수입니다.")
    private String deposit; // "5000" (만원 단위)

    private String rent; // "80" (월세일 경우)

    @NotBlank(message = "상세 요청사항은 필수입니다.")
    private String details;

    // DTO를 Request Entity로 변환하는 메소드
    public Request toEntity(User customer) {
        return Request.builder()
                .propertyType(this.propertyType)
                .transactionType(this.transactionType)
                .location(this.location)
                .deposit(this.deposit)
                .rent(this.rent)
                .details(this.details)
                .status("검토 중") // 신청서의 초기 상태
                .customer(customer) // 신청서를 작성한 고객(User)을 연결
                .build();
    }
}
