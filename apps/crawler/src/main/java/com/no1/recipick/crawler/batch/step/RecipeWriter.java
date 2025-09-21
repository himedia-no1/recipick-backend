package com.no1.recipick.crawler.batch.step;

import com.no1.recipick.crawler.domain.entity.*;
import com.no1.recipick.crawler.domain.repository.*;
import com.no1.recipick.crawler.dto.ProcessedRecipeDto;
import com.no1.recipick.crawler.dto.RecipeIngredientDto;
import com.no1.recipick.crawler.dto.RecipeStepDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecipeWriter implements ItemWriter<ProcessedRecipeDto> {

    private final RecipeRepository recipeRepository;
    private final RecipeLevelRepository recipeLevelRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends ProcessedRecipeDto> chunk) throws Exception {
        for (ProcessedRecipeDto processedRecipe : chunk) {
            try {
                if (processedRecipe.isValid()) {
                    saveValidRecipe(processedRecipe);
                } else {
                    markRecipeAsInvalid(processedRecipe.getRecipeId());
                }
            } catch (Exception e) {
                log.error("Failed to write recipe {}", processedRecipe.getRecipeId(), e);
                markRecipeAsInvalid(processedRecipe.getRecipeId());
            }
        }
    }

    private void saveValidRecipe(ProcessedRecipeDto processedRecipe) {
        log.info("Saving valid recipe: {}", processedRecipe.getRecipeId());

        // 1. Recipe 업데이트
        Recipe recipe = recipeRepository.findById(processedRecipe.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found: " + processedRecipe.getRecipeId()));

        RecipeLevel recipeLevel = getRecipeLevel(processedRecipe.getCrawledData().getLevel());

        recipe.updateRecipeData(
                processedRecipe.getCrawledData().getName(),
                processedRecipe.getCrawledData().getDescription(),
                processedRecipe.getCrawledData().getImageUrl(),
                processedRecipe.getCrawledData().getServing(),
                processedRecipe.getCrawledData().getTime(),
                recipeLevel
        );

        recipeRepository.save(recipe);

        // 2. RecipeIngredients 저장
        saveRecipeIngredients(recipe, processedRecipe.getMatchingResult().getMatchedIngredients());

        // 3. RecipeSteps 저장
        saveRecipeSteps(recipe, processedRecipe.getCrawledData().getSteps());

        log.info("Successfully saved recipe: {}", processedRecipe.getRecipeId());
    }

    private RecipeLevel getRecipeLevel(String levelKoreanName) {
        RecipeLevel.Level enumLevel = RecipeLevel.Level.fromKoreanName(levelKoreanName);
        if (enumLevel == null) {
            throw new RuntimeException("Invalid recipe level: " + levelKoreanName);
        }

        return recipeLevelRepository.findByValue(enumLevel.name())
                .orElseThrow(() -> new RuntimeException("Recipe level not found: " + enumLevel.name()));
    }

    private void saveRecipeIngredients(Recipe recipe, List<RecipeIngredientDto> ingredientDtos) {
        List<RecipeIngredient> ingredients = new ArrayList<>();

        for (RecipeIngredientDto dto : ingredientDtos) {
            IngredientCategory category = ingredientCategoryRepository.findById(dto.getIngredientCategoryId())
                    .orElseThrow(() -> new RuntimeException("Ingredient category not found: " + dto.getIngredientCategoryId()));

            RecipeIngredient ingredient = RecipeIngredient.of(recipe, dto.getName(), category);
            ingredients.add(ingredient);
        }

        recipeIngredientRepository.saveAll(ingredients);
        log.debug("Saved {} recipe ingredients", ingredients.size());
    }

    private void saveRecipeSteps(Recipe recipe, List<RecipeStepDto> stepDtos) {
        List<RecipeStep> steps = new ArrayList<>();

        for (RecipeStepDto dto : stepDtos) {
            RecipeStep step = RecipeStep.of(recipe, dto.getDescription(), dto.getImageUrl());
            steps.add(step);
        }

        recipeStepRepository.saveAll(steps);
        log.debug("Saved {} recipe steps", steps.size());
    }

    private void markRecipeAsInvalid(Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found: " + recipeId));

        recipe.markAsInvalid();
        recipeRepository.save(recipe);

        log.warn("Marked recipe as invalid: {}", recipeId);
    }
}