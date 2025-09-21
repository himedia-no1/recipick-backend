package com.no1.recipick.crawler.service.crawler;

import com.no1.recipick.crawler.domain.entity.IngredientCategory;
import com.no1.recipick.crawler.domain.repository.IngredientCategoryRepository;
import com.no1.recipick.crawler.dto.ProcessedRecipeDto.MatchingResult;
import com.no1.recipick.crawler.dto.RecipeIngredientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngredientMatchingService {

    private final IngredientCategoryRepository ingredientCategoryRepository;

    @Value("${crawler.ingredient.similarity.threshold:0.7}")
    private double similarityThreshold;

    public MatchingResult matchIngredients(List<String> crawledIngredients) {
        List<RecipeIngredientDto> matchedIngredients = new ArrayList<>();

        for (String ingredientName : crawledIngredients) {
            Optional<Integer> categoryId = findBestMatchingCategory(ingredientName);

            if (categoryId.isEmpty()) {
                log.warn("No matching category found for ingredient: {} (threshold: {})",
                         ingredientName, similarityThreshold);
                return MatchingResult.failure("재료 매칭 실패: " + ingredientName);
            }

            matchedIngredients.add(RecipeIngredientDto.of(ingredientName, categoryId.get()));
            log.debug("Matched ingredient '{}' to category ID: {}", ingredientName, categoryId.get());
        }

        return MatchingResult.success(matchedIngredients);
    }

    private Optional<Integer> findBestMatchingCategory(String ingredientName) {
        try {
            Optional<Object[]> result = ingredientCategoryRepository
                .findBestMatchByName(ingredientName, similarityThreshold);

            if (result.isPresent()) {
                Object[] row = result.get();
                log.debug("Raw query result: {}", java.util.Arrays.toString(row));

                Integer categoryId;
                String categoryValue;
                BigDecimal score;

                // Handle potential nested array structure
                Object firstElement = row[0];
                if (firstElement instanceof Object[]) {
                    Object[] nestedRow = (Object[]) firstElement;
                    categoryId = ((Number) nestedRow[0]).intValue();
                    categoryValue = (String) nestedRow[1];
                    // Handle both Double and BigDecimal for score
                    Object scoreObj = nestedRow[2];
                    score = scoreObj instanceof BigDecimal ? (BigDecimal) scoreObj :
                           BigDecimal.valueOf(((Number) scoreObj).doubleValue());
                } else {
                    categoryId = ((Number) row[0]).intValue();
                    categoryValue = (String) row[1];
                    // Handle both Double and BigDecimal for score
                    Object scoreObj = row[2];
                    score = scoreObj instanceof BigDecimal ? (BigDecimal) scoreObj :
                           BigDecimal.valueOf(((Number) scoreObj).doubleValue());
                }

                log.debug("Best match for '{}': {} (ID: {}, Score: {})",
                         ingredientName, categoryValue, categoryId, score);

                return Optional.of(categoryId);
            }

            return Optional.empty();

        } catch (Exception e) {
            log.error("Error finding matching category for ingredient: {}", ingredientName, e);
            return Optional.empty();
        }
    }
}