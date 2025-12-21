package com.harubang.harubangBackend.dto;

import com.harubang.harubangBackend.entity.Request;
import lombok.Getter;

import java.time.LocalDateTime;

// 중개사에게 신청서 목록을 응답하기 위한 DTO
@Getter
public class RequestResponseDto {

    private Long id;
    private String customerName; // 신청서 작성 고객 이름
    private String propertyType;
    private String transactionType;
    private String location;
    private String deposit;
    private String rent;
    private String details;
    private String status;
    private LocalDateTime createdAt; // 프론트에서 'date'로 사용

    // Request 엔티티를 DTO로 변환하는 생성자
    public RequestResponseDto(Request entity) {
        this.id = entity.getId();
        this.customerName = entity.getCustomer().getName(); // 고객 엔티티에서 이름만 추출
        this.propertyType = entity.getPropertyType();
        this.transactionType = entity.getTransactionType();
        this.location = entity.getLocation();
        this.deposit = entity.getDeposit();
        this.rent = entity.getRent();
        this.details = entity.getDetails();
        this.status = entity.getStatus();
        this.createdAt = entity.getCreatedAt();
    }
}