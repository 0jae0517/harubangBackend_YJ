package com.harubang.harubangBackend.service;

import com.harubang.harubangBackend.dto.PropertyCreateDto;
import com.harubang.harubangBackend.dto.PropertyResponseDto; // DTO 임포트
import com.harubang.harubangBackend.entity.Property;
import com.harubang.harubangBackend.entity.Role;
import com.harubang.harubangBackend.entity.User;
import com.harubang.harubangBackend.repository.PropertyRepository;
import com.harubang.harubangBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors; // Collectors 임포트

@Service
@Transactional
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    /**
     * 매물 생성 로직
     * @param createDto 매물 폼 데이터
     * @param userEmail JWT 토큰에서 추출한 사용자 이메일
     * @return 저장된 Property 엔티티
     */
    public Property createProperty(PropertyCreateDto createDto, String userEmail) {
        // 1. 사용자 이메일로 User 엔티티 조회
        User agent = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 해당 사용자의 역할이 AGENT인지 확인
        if (agent.getRole() != Role.AGENT) {
            throw new AccessDeniedException("매물을 등록할 권한이 없습니다.");
        }

        // 3. DTO를 Entity로 변환 (이때 User 정보가 함께 저장됨)
        Property property = createDto.toEntity(agent);

        // 4. 리포지토리를 통해 DB에 저장
        return propertyRepository.save(property);
    }

    /**
     * 중개사가 등록한 매물 목록 조회
     * @param userEmail JWT 토큰에서 추출한 사용자 이메일
     * @return 매물 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<PropertyResponseDto> getMyProperties(String userEmail) { // 반환 타입 변경
        User agent = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (agent.getRole() != Role.AGENT) {
            throw new AccessDeniedException("중개사만 조회할 수 있습니다.");
        }

        // 엔티티 리스트를 DTO 리스트로 변환하여 반환
        return propertyRepository.findByAgentId(agent.getId())
                .stream()
                .map(PropertyResponseDto::new) // Property -> PropertyResponseDto
                .collect(Collectors.toList());
    }

    /**
     * 매물 상세 조회
     * @param propertyId 매물 ID
     * @return 매물 DTO
     */
    @Transactional(readOnly = true)
    public PropertyResponseDto getPropertyById(Long propertyId) { // 반환 타입 변경
        // 엔티티를 DTO로 변환하여 반환
        return propertyRepository.findById(propertyId)
                .map(PropertyResponseDto::new) // Property -> PropertyResponseDto
                .orElseThrow(() -> new IllegalArgumentException("매물을 찾을 수 없습니다."));
    }

    /**
     * 매물 삭제
     * @param propertyId 매물 ID
     * @param userEmail JWT 토큰에서 추출한 사용자 이메일
     */
    public void deleteProperty(Long propertyId, String userEmail) {
        User agent = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("매물을 찾을 수 없습니다."));

        // 본인이 등록한 매물인지 확인
        if (!property.getAgent().getId().equals(agent.getId())) {
            throw new AccessDeniedException("본인이 등록한 매물만 삭제할 수 있습니다.");
        }

        propertyRepository.delete(property);
    }

    /**
     * 모든 매물 조회 (검색/필터링용 - 나중에 확장)
     * @return 전체 매물 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<PropertyResponseDto> getAllProperties() { // 반환 타입 변경
        // 엔티티 리스트를 DTO 리스트로 변환하여 반환
        return propertyRepository.findAll()
                .stream()
                .map(PropertyResponseDto::new)
                .collect(Collectors.toList());
    }
}