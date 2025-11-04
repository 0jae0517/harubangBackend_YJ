package com.harubang.harubangBackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "proposals")
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proposal_id")
    private Long id;

    // 제안 대상이 된 신청서 (Proposal과 Request는 다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    // 제안된 매물 (Proposal과 Property는 다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    // 제안한 공인중개사 (Proposal과 User는 다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false) // user_id 대신 agent_id 사용 가능
    private User agent;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 채팅방 ID (선택 사항, 별도 ChatRoom Entity와 연결하거나 간단히 Long 타입으로 저장)
    private Long chatRoomId;

    // 생성자 (Builder 패턴 사용)
    @Builder
    public Proposal(Request request, Property property, User agent, Long chatRoomId) {
        // 제안하는 중개사가 AGENT 역할인지 확인하는 로직 추가 가능
        if (agent.getRole() != Role.AGENT) {
            throw new IllegalArgumentException("제안은 공인중개사만 할 수 있습니다.");
        }
        // 제안 대상 매물이 해당 중개사의 매물인지 확인하는 로직 추가 가능
        if (!property.getAgent().equals(agent)) {
            throw new IllegalArgumentException("자신이 등록한 매물만 제안할 수 있습니다.");
        }

        this.request = request;
        this.property = property;
        this.agent = agent;
        this.chatRoomId = chatRoomId;

        // 연관관계 편의 메소드를 사용하여 양방향 관계 설정 (선택 사항)
        // request.getProposals().add(this);
        // property.getProposals().add(this);
        // agent.getProposals().add(this);
    }
}
