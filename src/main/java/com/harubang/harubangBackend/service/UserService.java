package com.harubang.harubangBackend.service;

import com.harubang.harubangBackend.dto.AgentLicenseApiResponseDto; // [추가]
import com.harubang.harubangBackend.dto.AgentSignUpDto;
import com.harubang.harubangBackend.dto.CustomerSignUpDto;
import com.harubang.harubangBackend.dto.LoginRequestDto;
import com.harubang.harubangBackend.entity.User;
// import com.harubang.harubangBackend.repository.RealEstateAgentLicenseRepository; // [삭제]
import com.harubang.harubangBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // private final RealEstateAgentLicenseRepository licenseRepository; // [삭제]
    private final AgentLicenseApiService agentLicenseApiService; // [추가] API 서비스 주입

    /**
     * 고객 회원가입 로직
     * @param requestDto 고객 회원가입 요청 데이터
     * @return 저장된 User 엔티티
     */
    @Transactional
    public User signUpCustomer(CustomerSignUpDto requestDto) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. DTO를 User Entity로 변환 (비밀번호 암호화)
        User user = requestDto.toEntity(passwordEncoder);

        // 3. DB에 저장
        return userRepository.save(user);
    }

    /**
     * [수정] 중개사 회원가입 로직 (API 연동)
     * @param requestDto 중개사 회원가입 요청 데이터
     * @return 저장된 User 엔티티
     */
    @Transactional
    public User signUpAgent(AgentSignUpDto requestDto) {

        // --- [수정] 중개사 자격 검증 로직 (오픈 API 호출) ---
        // 1. DTO에서 받은 개설등록번호로 오픈 API 호출
        AgentLicenseApiResponseDto.Item licenseInfo = agentLicenseApiService.getAgentInfo(requestDto.getRegistrationNumber());

        // 2. API 응답 결과 확인
        if (licenseInfo == null) {
            throw new IllegalArgumentException("유효하지 않은 개설등록번호이거나, 정보 조회에 실패했습니다.");
        }

        // 3. DTO의 정보와 API 응답 정보를 비교
        // [수정] API 응답 DTO의 필드명(getOfficeName, getRepresentativeName)으로 변경
        if (!licenseInfo.getOfficeName().equals(requestDto.getOfficeName())) {
            throw new IllegalArgumentException("사무소 상호명이 일치하지 않습니다.");
        }
        if (!licenseInfo.getRepresentativeName().equals(requestDto.getName())) {
            // DTO의 'name' 필드는 프론트에서 '대표자명'을 받습니다.
            throw new IllegalArgumentException("대표자명이 일치하지 않습니다.");
        }
        // ---------------------------------


        // 4. 이메일 중복 체크 (기존 로직)
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 5. DTO를 User Entity로 변환 (기존 로직)
        User user = requestDto.toEntity(passwordEncoder);

        // 6. DB에 저장 (기존 로직)
        return userRepository.save(user);
    }

    /**
     * 로그인 로직 (수동 검증)
     * @param requestDto 로그인 요청 데이터
     * @return 인증된 User 엔티티
     */
    @Transactional(readOnly = true) // 로그인_조회만 하므로 readOnly
    public User login(LoginRequestDto requestDto) {

        // 1. 이메일로 사용자 찾기
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 로그인 성공, User 객체 반환
        return user;
    }
}