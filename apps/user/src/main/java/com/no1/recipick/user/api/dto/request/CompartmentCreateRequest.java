package com.no1.recipick.user.api.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompartmentCreateRequest {

    @NotNull(message = "보관실 타입 ID는 필수입니다.")
    private Integer compartmentTypeId;

    @NotBlank(message = "보관칸 이름은 필수입니다.")
    @Size(min = 1, max = 100, message = "보관칸 이름은 1-100자 사이여야 합니다.")
    private String name;
}