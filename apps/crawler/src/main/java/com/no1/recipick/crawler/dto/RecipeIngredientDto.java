package com.no1.recipick.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientDto {

    private String name;
    private Integer ingredientCategoryId;

    public static RecipeIngredientDto of(String name, Integer ingredientCategoryId) {
        return RecipeIngredientDto.builder()
                .name(name)
                .ingredientCategoryId(ingredientCategoryId)
                .build();
    }
}