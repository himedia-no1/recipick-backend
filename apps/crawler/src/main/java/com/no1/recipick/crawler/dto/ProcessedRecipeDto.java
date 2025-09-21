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
public class ProcessedRecipeDto {

    private Integer recipeId;
    private CrawledRecipeDto crawledData;
    private MatchingResult matchingResult;
    private boolean isValid;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchingResult {
        private boolean isSuccess;
        private List<RecipeIngredientDto> matchedIngredients;
        private String failureReason;

        public static MatchingResult success(List<RecipeIngredientDto> matchedIngredients) {
            return MatchingResult.builder()
                    .isSuccess(true)
                    .matchedIngredients(matchedIngredients)
                    .build();
        }

        public static MatchingResult failure(String reason) {
            return MatchingResult.builder()
                    .isSuccess(false)
                    .failureReason(reason)
                    .build();
        }
    }
}