package com.harubang.harubangBackend.dto;

import com.harubang.harubangBackend.entity.Role;
import com.harubang.harubangBackend.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

// 고객 회원가입 요청 DTO
@Getter
@Setter
public class CustomerSignUpDto extends BaseSignUpDto {

    // BaseSignUpDto의 필드 외에 고객 전용 필드가 있다면 여기에 추가
    // (지금은 없음)

    // DTO를 User Entity로 변환하는 메소드 (고객용)
    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(getEmail())
                .password(passwordEncoder.encode(getPassword())) // 비밀번호 암호화
                .name(getName())
                .phone(getPhone())
                .role(Role.CUSTOMER) // 역할은 CUSTOMER로 고정
                .build();
    }
}
