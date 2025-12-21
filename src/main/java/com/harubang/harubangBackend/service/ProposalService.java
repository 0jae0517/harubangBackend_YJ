package com.harubang.harubangBackend.service;

import com.harubang.harubangBackend.dto.ProposalCreateDto;
import com.harubang.harubangBackend.dto.ProposalResponseDto; // 1. DTO 임포트
import com.harubang.harubangBackend.entity.Proposal;
import com.harubang.harubangBackend.entity.Property;
import com.harubang.harubangBackend.entity.Request;
import com.harubang.harubangBackend.entity.User;
import com.harubang.harubangBackend.repository.ProposalRepository;
import com.harubang.harubangBackend.repository.PropertyRepository;
import com.harubang.harubangBackend.repository.RequestRepository;
import com.harubang.harubangBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; // 2. List 임포트
import java.util.stream.Collectors; // 3. Collectors 임포트

@Service
@Transactional
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final PropertyRepository propertyRepository;

    /**
     * 매물 제안 생성
     */
    public Proposal createProposal(ProposalCreateDto dto, String agentEmail) {

        // 1. 엔티티 조회
        User agent = userRepository.findByEmail(agentEmail)
                .orElseThrow(() -> new IllegalArgumentException("중개사 정보를 찾을 수 없습니다."));

        Request request = requestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new IllegalArgumentException("신청서 정보를 찾을 수 없습니다."));

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new IllegalArgumentException("매물 정보를 찾을 수 없습니다."));

        // 2. 권한 검증 (본인 매물이 맞는지)
        if (!property.getAgent().getId().equals(agent.getId())) {
            throw new AccessDeniedException("본인이 등록한 매물만 제안할 수 있습니다.");
        }

        // 3. 제안 엔티티 생성
        Proposal proposal = Proposal.builder()
                .request(request)
                .property(property)
                .agent(agent)
                .comment(dto.getComment())
                .chatRoomId(request.getId() + agent.getId()) // 임시 채팅방 ID 생성
                .build();

        // 4. 신청서 상태 업데이트 (매우 중요!)
        // 현재 제안 건수 + 1
        int proposalCount = proposalRepository.findByRequestId(request.getId()).size() + 1;
        request.updateStatus("제안 도착 (" + proposalCount + "건)");

        // 5. 제안 저장
        return proposalRepository.save(proposal);
    }

    // --- [신규] 고객이 받은 제안 목록 조회 ---
    /**
     * 특정 신청서(requestId)에 대해 고객이 받은 제안 목록 조회
     * @param requestId 신청서 ID
     * @param customerEmail (로그인한 고객 이메일)
     * @return ProposalResponseDto 리스트
     */
    @Transactional(readOnly = true)
    public List<ProposalResponseDto> getProposalsByRequest(Long requestId, String customerEmail) {
        // 1. DTO를 반환할 List<Proposal> 조회
        List<Proposal> proposals = proposalRepository.findByRequestId(requestId);

        // 2. (중요) 보안 검증: 요청한 고객이 이 신청서의 주인인지 확인
        if (!proposals.isEmpty()) {
            // 첫 번째 제안을 기준으로 신청서의 고객 이메일 확인
            String ownerEmail = proposals.get(0).getRequest().getCustomer().getEmail();
            if (!ownerEmail.equals(customerEmail)) {
                throw new AccessDeniedException("본인의 신청서에 대한 제안만 조회할 수 있습니다.");
            }
        } else {
            // 제안이 없는 경우, 신청서가 존재하는지, 그리고 그 주인이 맞는지 별도 확인
            Request request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new IllegalArgumentException("신청서를 찾을 수 없습니다."));
            if (!request.getCustomer().getEmail().equals(customerEmail)) {
                throw new AccessDeniedException("본인의 신청서에 대한 제안만 조회할 수 있습니다.");
            }
        }

        // 3. DTO로 변환하여 반환
        return proposals.stream()
                .map(ProposalResponseDto::new)
                .collect(Collectors.toList());
    }

    // [TODO] 중개사가 보낸 제안 목록 조회
}