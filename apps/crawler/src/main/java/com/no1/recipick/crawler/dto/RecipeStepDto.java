package com.no1.recipick.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStepDto {

    private String description;
    private String imageUrl;

    public static RecipeStepDto of(String description, String imageUrl) {
        return RecipeStepDto.builder()
                .description(description)
                .imageUrl(imageUrl)
                .build();
    }
}