package com.harubang.harubangBackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long id;

    @Column(nullable = false)
    private String name; // 매물 이름 (예: 역삼동 래미안 101동 502호)

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String propertyType; // 매물 종류 (아파트, 오피스텔 등) - Enum 사용 고려

    @Column(nullable = false)
    private String transactionType; // 거래 유형 (전세, 월세, 매매) - Enum 사용 고려

    @Column(nullable = false)
    private String price; // 가격 정보 (예: "5억", "2000만 / 80만") - 별도 필드 분리 고려

    private String deposit; // 보증금 (월세/전세)
    private String monthlyRent; // 월세 (월세)
    private String salePrice; // 매매가 (매매)

    @Column(nullable = false)
    private String area; // 면적 (예: "84㎡")

    private Integer rooms; // 방 개수
    private Integer baths; // 욕실 개수
    private String floor; // 해당 층

    @Lob // 긴 텍스트 저장을 위해 사용
    @Column(columnDefinition = "TEXT")
    private String description; // 상세 설명

    // 사진 URL 목록 (간단하게 String으로 저장, 복잡해지면 별도 Entity 고려)
    // @ElementCollection // 간단한 값 타입 컬렉션 매핑
    // @CollectionTable(name = "property_images", joinColumns = @JoinColumn(name = "property_id"))
    // @Column(name = "image_url")
    // private List<String> imageUrls = new ArrayList<>();

    // 매물을 등록한 공인중개사 (Property와 User는 다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "user_id", nullable = false) // 외래 키 컬럼명 지정 및 not null 설정
    private User agent;

    // 이 매물이 포함된 제안 목록 (Property는 Proposal을 여러 개 가질 수 있음 - OneToMany)
    @OneToMany(mappedBy = "property") // Proposal 엔티티의 'property' 필드와 연결됨
    private List<Proposal> proposals = new ArrayList<>();

    // 생성자 (Builder 패턴 사용)
    @Builder
    public Property(String name, String address, String propertyType, String transactionType, String price, String deposit, String monthlyRent, String salePrice, String area, Integer rooms, Integer baths, String floor, String description, User agent) {
        this.name = name;
        this.address = address;
        this.propertyType = propertyType;
        this.transactionType = transactionType;
        this.price = price; // 가격 표현 방식 논의 필요
        this.deposit = deposit;
        this.monthlyRent = monthlyRent;
        this.salePrice = salePrice;
        this.area = area;
        this.rooms = rooms;
        this.baths = baths;
        this.floor = floor;
        this.description = description;
        this.agent = agent;
    }

    // 매물 정보 업데이트 메소드 (필요에 따라 추가)
    public void updatePropertyInfo(/* 업데이트할 필드들 */) {
        // ... 필드 업데이트 로직 ...
    }

    // 연관관계 편의 메소드 (선택 사항)
    public void setAgent(User agent) {
        this.agent = agent;
        // agent.getProperties().add(this); // User 엔티티에도 연관관계 설정 시 필요
    }
}
