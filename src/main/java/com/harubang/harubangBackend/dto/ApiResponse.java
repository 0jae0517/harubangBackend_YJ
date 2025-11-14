package com.harubang.harubangBackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder // Builder 패턴을 사용하여 ApiResponse 객체를 쉽게 생성
@JsonInclude(JsonInclude.Include.NON_NULL) // data 필드가 null일 경우 JSON 응답에 포함시키지 않음
public class ApiResponse<T> {

    @Builder.Default // success 필드의 기본값을 true로 설정
    private boolean success = true;
    private String message;
    private T data; // 제네릭 타입 T를 사용하여 모든 종류의 데이터(DTO)를 담을 수 있음

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now(); // 응답 시간

    // 성공 응답을 쉽게 생성하기 위한 정적 메소드 (데이터 포함)
    public static <T> ApiResponse<T> createSuccess(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    // 성공 응답을 쉽게 생성하기 위한 정적 메소드 (데이터 없음)
    public static <T> ApiResponse<T> createSuccessWithNoData(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    // 실패 응답을 쉽게 생성하기 위한 정적 메소드 (GlobalExceptionHandler에서 사용)
    public static <T> ApiResponse<T> createError(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}