package com.no1.recipick.crawler.batch.step;

import com.no1.recipick.crawler.domain.entity.Recipe;
import com.no1.recipick.crawler.domain.entity.RecipeLevel;
import com.no1.recipick.crawler.domain.repository.RecipeLevelRepository;
import com.no1.recipick.crawler.dto.CrawledRecipeDto;
import com.no1.recipick.crawler.dto.ProcessedRecipeDto;
import com.no1.recipick.crawler.service.crawler.IngredientMatchingService;
import com.no1.recipick.crawler.service.crawler.RecipeCrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecipeProcessor implements ItemProcessor<Recipe, ProcessedRecipeDto> {

    private final RecipeCrawlerService recipeCrawlerService;
    private final IngredientMatchingService ingredientMatchingService;
    private final RecipeLevelRepository recipeLevelRepository;

    @Override
    public ProcessedRecipeDto process(Recipe recipe) throws Exception {
        log.info("Processing recipe with searchIdentity: {}", recipe.getSearchIdentity());

        try {
            // 1. 크롤링 실행
            CrawledRecipeDto crawledData = recipeCrawlerService.crawlRecipe(recipe.getSearchIdentity());

            // 2. 필수 데이터 검증
            if (!crawledData.hasRequiredFields()) {
                log.warn("Recipe {} missing required fields", recipe.getSearchIdentity());
                return ProcessedRecipeDto.builder()
                        .recipeId(recipe.getId())
                        .crawledData(crawledData)
                        .matchingResult(ProcessedRecipeDto.MatchingResult.failure("필수 필드 누락"))
                        .isValid(false)
                        .build();
            }

            // 3. 레시피 난이도 검증
            if (!isValidRecipeLevel(crawledData.getLevel())) {
                log.warn("Recipe {} has invalid level: {}", recipe.getSearchIdentity(), crawledData.getLevel());
                return ProcessedRecipeDto.builder()
                        .recipeId(recipe.getId())
                        .crawledData(crawledData)
                        .matchingResult(ProcessedRecipeDto.MatchingResult.failure("유효하지 않은 난이도: " + crawledData.getLevel()))
                        .isValid(false)
                        .build();
            }

            // 4. 재료 매칭 검증
            ProcessedRecipeDto.MatchingResult matchingResult =
                ingredientMatchingService.matchIngredients(crawledData.getIngredients());

            if (!matchingResult.isSuccess()) {
                log.warn("Recipe {} ingredient matching failed: {}",
                         recipe.getSearchIdentity(), matchingResult.getFailureReason());
                return ProcessedRecipeDto.builder()
                        .recipeId(recipe.getId())
                        .crawledData(crawledData)
                        .matchingResult(matchingResult)
                        .isValid(false)
                        .build();
            }

            // 5. 모든 검증 통과
            log.info("Recipe {} processed successfully", recipe.getSearchIdentity());
            return ProcessedRecipeDto.builder()
                    .recipeId(recipe.getId())
                    .crawledData(crawledData)
                    .matchingResult(matchingResult)
                    .isValid(true)
                    .build();

        } catch (Exception e) {
            log.error("Failed to process recipe {}", recipe.getSearchIdentity(), e);
            return ProcessedRecipeDto.builder()
                    .recipeId(recipe.getId())
                    .matchingResult(ProcessedRecipeDto.MatchingResult.failure("처리 중 오류 발생: " + e.getMessage()))
                    .isValid(false)
                    .build();
        }
    }

    private boolean isValidRecipeLevel(String level) {
        if (level == null || level.trim().isEmpty()) {
            return false;
        }

        RecipeLevel.Level enumLevel = RecipeLevel.Level.fromKoreanName(level);
        if (enumLevel == null) {
            return false;
        }

        return recipeLevelRepository.findByValue(enumLevel.name()).isPresent();
    }
}