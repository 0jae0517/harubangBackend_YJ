package com.harubang.harubangBackend.service;

import com.harubang.harubangBackend.dto.PropertyCreateDto;
import com.harubang.harubangBackend.entity.Property;
import com.harubang.harubangBackend.entity.Role;
import com.harubang.harubangBackend.entity.User;
import com.harubang.harubangBackend.repository.PropertyRepository;
import com.harubang.harubangBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new AccessDeniedException("매물을 등록할 권한이 없습니다."); // 403 Forbidden
        }

        // 3. DTO를 Entity로 변환 (이때 User 정보가 함께 저장됨)
        Property property = createDto.toEntity(agent);

        // 4. 리포지토리를 통해 DB에 저장
        return propertyRepository.save(property);
    }

    // [TODO]
    // 1. 내 매물 목록 조회 (중개사용)
}