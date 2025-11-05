package com.harubang.harubangBackend.dto;

import com.harubang.harubangBackend.entity.Property;
import com.harubang.harubangBackend.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// 중개사가 매물 등록 시 보낼 데이터를 받는 객체
@Getter
@Setter
public class PropertyCreateDto {

    @NotBlank
    private String propertyType; // "아파트", "오피스텔" 등

    @NotBlank
    private String transactionType; // "전세", "월세", "매매"

    @NotBlank
    private String address; // "서울특별시 강남구 역삼동 123-45 ..."

    @NotBlank
    private String deposit; // 보증금/매매가 (프론트에서 "5000" 문자열로 줌)

    private String rent; // 월세 (월세일 경우)

    @NotBlank
    private String area; // 전용 면적 (예: "84")

    @NotNull
    private Integer rooms; // 방 개수

    @NotNull
    private Integer baths; // 욕실 개수

    @NotBlank
    private String floor; // 해당 층 (예: "5")

    @NotBlank
    private String description; // 상세 설명

    // (사진(image)은 일단 제외하고 텍스트 정보만 먼저 구현합니다.)

    // DTO를 Property Entity로 변환하는 메소드
    public Property toEntity(User agent) {

        // [개선] 프론트에서 deposit, rent, price를 어떻게 주는지에 따라
        // price 필드를 조합하는 로직이 필요합니다.
        // 우선은 프론트에서 받은 값을 그대로 사용.
        String priceDisplay = transactionType.equals("월세") ?
                deposit + " / " + rent : deposit;

        return Property.builder()
                .name(this.address) // 우선 주소를 이름으로 사용
                .address(this.address)
                .propertyType(this.propertyType)
                .transactionType(this.transactionType)
                .price(priceDisplay) // 임시 가격 표시
                .deposit(this.deposit)
                .monthlyRent(this.rent)
                // .salePrice(this.transactionType.equals("매매") ? this.deposit : null)
                .area(this.area + "㎡") // 단위 붙이기
                .rooms(this.rooms)
                .baths(this.baths)
                .floor(this.floor)
                .description(this.description)
                .agent(agent) // 매물을 등록한 중개사(User)를 연결
                .build();
    }
}