package com.harubang.harubangBackend.dto;

import com.harubang.harubangBackend.entity.Proposal;
import com.harubang.harubangBackend.entity.Property;
import lombok.Getter;

// 고객의 '받은 제안 목록' 페이지를 위한 DTO
@Getter
public class ProposalResponseDto {

    private Long id; // 제안 ID
    private String officeName; // <-- 1. (신규) 중개사무소 이름
    private String representativeName; // <-- 2. (agentName -> representativeName)
    private Long chatRoomId; // 채팅방 ID
    private String comment; // 중개사 코멘트
    private PropertyDetailsDto property; // 매물 상세 정보

    // Proposal 엔티티를 DTO로 변환하는 생성자
    public ProposalResponseDto(Proposal entity) {
        this.id = entity.getId();
        // 3. User 엔티티에서 officeName과 name을 모두 가져옵니다.
        this.officeName = entity.getAgent().getOfficeName();
        this.representativeName = entity.getAgent().getName();
        this.chatRoomId = entity.getChatRoomId();
        this.comment = entity.getComment();
        this.property = new PropertyDetailsDto(entity.getProperty()); // 매물 엔티티를 DTO로 변환
    }

    // 매물 정보를 담을 내부 DTO
    @Getter
    public static class PropertyDetailsDto {
        private Long id;
        private String name;
        private String propertyType;
        private String transactionType; // 프론트에서 "deal"로 사용
        private String price;
        private String area;
        private Integer rooms;
        private Integer baths;

        public PropertyDetailsDto(Property property) {
            this.id = property.getId();
            this.name = property.getName();
            this.propertyType = property.getPropertyType();
            this.transactionType = property.getTransactionType();
            this.price = property.getPrice();
            this.area = property.getArea();
            this.rooms = property.getRooms();
            this.baths = property.getBaths();
        }
    }
}