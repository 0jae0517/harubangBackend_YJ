package com.harubang.harubangBackend.service;

import com.harubang.harubangBackend.dto.AgentSignUpDto;
import com.harubang.harubangBackend.dto.CustomerSignUpDto;
import com.harubang.harubangBackend.dto.LoginRequestDto;
import com.harubang.harubangBackend.entity.User;
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
     * 중개사 회원가입 로직
     * @param requestDto 중개사 회원가입 요청 데이터
     * @return 저장된 User 엔티티
     */
    @Transactional
    public User signUpAgent(AgentSignUpDto requestDto) {
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