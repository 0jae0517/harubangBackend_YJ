package com.harubang.harubangBackend.dto;

import com.harubang.harubangBackend.entity.Role;
import com.harubang.harubangBackend.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

// 중개사 회원가입 요청 DTO
@Getter
@Setter
public class AgentSignUpDto extends BaseSignUpDto {

    // --- 중개사 회원가입 시 필수 추가 정보 ---
    @NotBlank(message = "중개등록번호는 필수 입력 값입니다.")
    private String registrationNumber; // 중개등록번호

    @NotBlank(message = "사무소 이름은 필수 입력 값입니다.")
    private String officeName; // 상호명

    @NotBlank(message = "사무소 주소는 필수 입력 값입니다.")
    private String officeAddress; // 사무소 주소

    // DTO를 User Entity로 변환하는 메소드 (중개사용)
    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(getEmail())
                .password(passwordEncoder.encode(getPassword())) // 비밀번호 암호화
                .name(getName())
                .phone(getPhone())
                .role(Role.AGENT) // 역할은 AGENT로 고정
                .registrationNumber(this.registrationNumber)
                .officeName(this.officeName)
                .officeAddress(this.officeAddress)
                .build();
    }
}
