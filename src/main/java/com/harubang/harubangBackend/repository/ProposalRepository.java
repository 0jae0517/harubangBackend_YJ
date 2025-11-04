package com.harubang.harubangBackend.repository;

import com.harubang.harubangBackend.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    // JpaRepository<Proposal, Long>
    // -> Proposal 엔티티를 관리하고, Proposal의 ID 타입은 Long이다.

    // 특정 신청서(Request) ID에 해당하는 모든 제안 목록을 찾는다. (고객이 받은 제안 목록)
    List<Proposal> findByRequestId(Long requestId);

    // 특정 공인중개사(Agent) ID가 보낸 모든 제안 목록을 찾는다.
    List<Proposal> findByAgentId(Long agentId);
}
