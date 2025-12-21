package com.harubang.harubangBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProposalCreateDto {

    @NotNull(message = "신청서 ID는 필수입니다.")
    private Long requestId;

    @NotNull(message = "매물 ID는 필수입니다.")
    private Long propertyId;

    @NotBlank(message = "코멘트를 작성해주세요.")
    private String comment; // 고객에게 보낼 코멘트
}