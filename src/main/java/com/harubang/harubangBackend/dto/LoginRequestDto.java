package com.harubang.harubangBackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// 프론트엔드에서 로그인 시 보낼 데이터를 받는 객체
@Getter
@Setter
public class LoginRequestDto {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
}
