package com.no1.recipick.user.api.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class IngredientUpdateRequest {

    @Size(min = 1, max = 100, message = "식재료 이름은 1-100자 사이여야 합니다.")
    private String name;

    @Size(max = 500, message = "메모는 500자 이하여야 합니다.")
    private String memo;

    private LocalDateTime expirationDate;
}