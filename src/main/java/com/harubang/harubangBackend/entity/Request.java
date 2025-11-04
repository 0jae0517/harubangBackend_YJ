package com.harubang.harubangBackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column(nullable = false)
    private String propertyType; // 희망 매물 종류

    @Column(nullable = false)
    private String transactionType; // 희망 거래 유형

    @Column(nullable = false)
    private String location; // 희망 지역

    @Column(nullable = false)
    private String deposit; // 희망 보증금/매매가 (프론트에서 '만원' 제외하고 숫자만 저장 고려)

    private String rent; // 희망 월세 (프론트에서 '만원' 제외하고 숫자만 저장 고려)

    @Lob
    @Column(columnDefinition = "TEXT")
    private String details; // 상세 요청사항

    @Column(nullable = false)
    private String status; // 신청서 상태 (예: "검토 중", "제안 도착 (2건)")

    @CreationTimestamp // 엔티티 생성 시 자동으로 현재 시간 저장
    private LocalDateTime createdAt;

    // 신청서를 작성한 고객 (Request와 User는 다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User customer;

    // 이 신청서에 대한 제안 목록 (Request는 Proposal을 여러 개 가질 수 있음 - OneToMany)
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Proposal> proposals = new ArrayList<>();

    // 생성자 (Builder 패턴 사용)
    @Builder
    public Request(String propertyType, String transactionType, String location, String deposit, String rent, String details, String status, User customer) {
        this.propertyType = propertyType;
        this.transactionType = transactionType;
        this.location = location;
        this.deposit = deposit;
        this.rent = rent;
        this.details = details;
        this.status = (status != null) ? status : "검토 중"; // 기본 상태 설정
        this.customer = customer;
    }

    // 신청서 상태 업데이트 메소드
    public void updateStatus(String status) {
        this.status = status;
    }

    // 연관관계 편의 메소드 (선택 사항)
    public void setCustomer(User customer) {
        this.customer = customer;
        // customer.getRequests().add(this); // User 엔티티에도 연관관계 설정 시 필요
    }
}
