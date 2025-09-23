package com.no1.recipick.user.api.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CookingCompleteRequest {

    @NotNull(message = "요리한 레시피 ID는 필수입니다.")
    private Integer recipeId;

    @NotEmpty(message = "사용된 식재료 목록은 필수입니다.")
    private List<UsedIngredientInfo> usedIngredients;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UsedIngredientInfo {
        @NotNull(message = "사용된 식재료 ID는 필수입니다.")
        private Integer ingredientId;
    }
}