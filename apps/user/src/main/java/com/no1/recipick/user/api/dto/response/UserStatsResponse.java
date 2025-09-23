package com.no1.recipick.user.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {
    private Long totalRecipesCooked;
    private Long totalFavoriteRecipes;
    private Long totalFridges;
    private Long totalIngredients;
}