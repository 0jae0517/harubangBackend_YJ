package com.harubang.harubangBackend.service;

import com.harubang.harubangBackend.dto.RequestCreateDto;
import com.harubang.harubangBackend.entity.Request;
import com.harubang.harubangBackend.entity.Role;
import com.harubang.harubangBackend.entity.User;
import com.harubang.harubangBackend.repository.RequestRepository;
import com.harubang.harubangBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    /**
     * 신청서 생성 로직
     * @param requestDto 신청서 폼 데이터
     * @param userEmail JWT 토큰에서 추출한 사용자 이메일
     * @return 저장된 Request 엔티티
     */
    public Request createRequest(RequestCreateDto requestDto, String userEmail) {
        // 1. 사용자 이메일로 User 엔티티 조회
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 해당 사용자의 역할이 CUSTOMER인지 확인 (선택적이지만 안전함)
        if (customer.getRole() != Role.CUSTOMER) {
            throw new AccessDeniedException("신청서를 작성할 권한이 없습니다."); // 403 Forbidden
        }

        // 3. DTO를 Entity로 변환 (이때 User 정보가 함께 저장됨)
        Request request = requestDto.toEntity(customer);

        // 4. 리포지토리를 통해 DB에 저장
        return requestRepository.save(request);
    }

    // [TODO]
    // 1. 내 신청서 목록 조회 (마이페이지용)
    // 2. 전체 신청서 목록 조회 (중개사용)
}
