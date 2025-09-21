package com.no1.recipick.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawledRecipeDto {

    private String name;
    private String description;
    private String imageUrl;
    private String serving;
    private String time;
    private String level;
    private List<String> ingredients;
    private List<RecipeStepDto> steps;

    public boolean hasRequiredFields() {
        return name != null && !name.trim().isEmpty() &&
               description != null && !description.trim().isEmpty() &&
               imageUrl != null && !imageUrl.trim().isEmpty() &&
               ingredients != null && !ingredients.isEmpty() &&
               steps != null && !steps.isEmpty();
    }
}