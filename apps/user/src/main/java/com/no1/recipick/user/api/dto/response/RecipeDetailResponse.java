package com.no1.recipick.user.api.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RecipeDetailResponse {

    private Integer recipeId;
    private String name;
    private String description;
    private String imageUrl;
    private String serving;
    private String time;
    private String difficulty;
    private List<IngredientInfo> ingredients;
    private List<StepInfo> steps;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class IngredientInfo {
        private String name;
        private String category;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class StepInfo {
        private Integer stepNumber;
        private String description;
        private String imageUrl;
    }
}