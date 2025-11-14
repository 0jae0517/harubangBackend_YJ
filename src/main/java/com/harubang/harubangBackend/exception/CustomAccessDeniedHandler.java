package com.harubang.harubangBackend.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature; // [추가]
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.harubang.harubangBackend.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    // [수정] ObjectMapper 초기화 방식을 static final로 변경하고 설정 추가
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            // [핵심] 이 설정을 추가해야 LocalDateTime이 "[...]" 배열이 아닌 "..." 문자열로 변환됩니다.
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        ApiResponse<Void> apiResponse = ApiResponse.createError("접근 권한이 없습니다.");

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        try (OutputStream os = response.getOutputStream()) {
            objectMapper.writeValue(os, apiResponse);
            os.flush();
        }
    }
}