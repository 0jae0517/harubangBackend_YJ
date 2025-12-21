package com.harubang.harubangBackend.dto;

import com.harubang.harubangBackend.entity.Property;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 프론트엔드에 매물 정보를 응답하기 위한 DTO
@Getter
@NoArgsConstructor
public class PropertyResponseDto {

    private Long id;
    private String name;
    private String address;
    private String propertyType;
    private String transactionType;
    private String price;
    private String deposit;
    private String monthlyRent;
    private String area;
    private Integer rooms;
    private Integer baths;
    private String floor;
    private String description;

    // Property 엔티티를 DTO로 변환하는 생성자
    public PropertyResponseDto(Property entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.address = entity.getAddress();
        this.propertyType = entity.getPropertyType();
        this.transactionType = entity.getTransactionType();
        this.price = entity.getPrice();
        this.deposit = entity.getDeposit();
        this.monthlyRent = entity.getMonthlyRent();
        this.area = entity.getArea();
        this.rooms = entity.getRooms();
        this.baths = entity.getBaths();
        this.floor = entity.getFloor();
        this.description = entity.getDescription();
    }
}