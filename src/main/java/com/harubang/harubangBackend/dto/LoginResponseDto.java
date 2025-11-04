package com.harubang.harubangBackend.dto;

import com.harubang.harubangBackend.entity.Role;
import lombok.Builder;
import lombok.Getter;

// 로그인 성공 시 프론트엔드에 반환할 정보 (토큰, 사용자 역할 등)
@Getter
public class LoginResponseDto {

    private String accessToken;
    private String userEmail;
    private String userName;
    private Role userRole;

    @Builder
    public LoginResponseDto(String accessToken, String userEmail, String userName, Role userRole) {
        this.accessToken = accessToken;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userRole = userRole;
    }
}