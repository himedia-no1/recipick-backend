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
public class RecipeResponse {

    private Integer recipeId;
    private String name;
    private String description;
    private String imageUrl;
    private String difficulty;
    private List<String> requiredIngredients;
    private Integer missingIngredientsCount;
}