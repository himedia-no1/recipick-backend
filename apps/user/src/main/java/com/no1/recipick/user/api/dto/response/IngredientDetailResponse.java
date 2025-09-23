package com.no1.recipick.user.api.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class IngredientDetailResponse {

    private Integer ingredientId;
    private Integer compartmentId;
    private Integer ingredientTypeId;
    private String name;
    private Integer ingredientCategoryId;
    private String memo;
    private LocalDateTime expirationDate;
    private Integer ingredientStateId;
    private LocalDateTime createdAt;
}