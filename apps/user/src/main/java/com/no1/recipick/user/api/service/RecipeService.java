package com.no1.recipick.user.api.service;

import com.no1.recipick.user.api.domain.entity.*;
import com.no1.recipick.user.api.domain.repository.*;
import com.no1.recipick.user.api.dto.request.CookingCompleteRequest;
import com.no1.recipick.user.api.dto.request.RecipeRatingRequest;
import com.no1.recipick.user.api.dto.response.*;
import com.no1.recipick.user.api.exception.BusinessException;
import com.no1.recipick.user.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final FridgeRepository fridgeRepository;
    private final IngredientRepository ingredientRepository;
    private final FavoriteRecipeRepository favoriteRecipeRepository;
    private final CookingHistoryRepository cookingHistoryRepository;
    private final RecipeRatingRepository recipeRatingRepository;
    private final RecipeLevelRepository recipeLevelRepository;
    private final IngredientStateRepository ingredientStateRepository;

    @Transactional(readOnly = true)
    public PagedResponse<RecipeResponse> getRecipes(int page, int size, String difficulty) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage;

        if (difficulty != null) {
            RecipeLevel recipeLevel = recipeLevelRepository.findByValue(difficulty.toUpperCase())
                    .orElseThrow(() -> new ResourceNotFoundException("레시피 난이도를 찾을 수 없습니다."));
            recipePage = recipeRepository.findByRecipeLevelAndIsValidTrueOrderByIdDesc(recipeLevel, pageable);
        } else {
            recipePage = recipeRepository.findByIsValidTrueOrderByIdDesc(pageable);
        }

        List<RecipeResponse> recipes = recipePage.getContent().stream()
                .map(this::mapToRecipeResponse)
                .collect(Collectors.toList());

        return PagedResponse.of(recipes, (int) recipePage.getTotalElements(),
                page, recipePage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public PagedResponse<RecipeResponse> getRecommendedRecipes(Integer userId, int page, int size,
                                                             String difficulty, Integer fridgeId) {
        User user = getUserById(userId);
        Fridge fridge;

        if (fridgeId != null) {
            fridge = getFridgeById(fridgeId);
            validateFridgeAccess(fridge, user);
        } else {
            fridge = fridgeRepository.findByOwnerAndIsDefaultTrueAndIsDeletedFalse(user)
                    .orElseThrow(() -> new ResourceNotFoundException("기본 냉장고를 찾을 수 없습니다."));
        }

        // 냉장고의 식재료 이름들 가져오기
        List<String> availableIngredients = getAvailableIngredientNames(fridge);

        if (availableIngredients.isEmpty()) {
            return PagedResponse.of(List.of(), 0, page, 0);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage = recipeRepository.findRecipesByAvailableIngredients(availableIngredients, pageable);

        List<RecipeResponse> recipes = recipePage.getContent().stream()
                .map(recipe -> mapToRecipeResponseWithMissingIngredients(recipe, availableIngredients))
                .collect(Collectors.toList());

        return PagedResponse.of(recipes, (int) recipePage.getTotalElements(),
                page, recipePage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public RecipeDetailResponse getRecipeDetail(Integer recipeId) {
        Recipe recipe = getRecipeById(recipeId);

        List<RecipeDetailResponse.IngredientInfo> ingredients = recipe.getRecipeIngredients().stream()
                .map(ri -> RecipeDetailResponse.IngredientInfo.builder()
                        .name(ri.getName())
                        .category(ri.getCategory())
                        .build())
                .collect(Collectors.toList());

        List<RecipeDetailResponse.StepInfo> steps = recipe.getRecipeSteps().stream()
                .sorted((s1, s2) -> s1.getStepNumber().compareTo(s2.getStepNumber()))
                .map(rs -> RecipeDetailResponse.StepInfo.builder()
                        .stepNumber(rs.getStepNumber())
                        .description(rs.getDescription())
                        .imageUrl(rs.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        return RecipeDetailResponse.builder()
                .recipeId(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .imageUrl(recipe.getImage())
                .serving(recipe.getServing())
                .time(recipe.getTime())
                .difficulty(recipe.getRecipeLevel() != null ? recipe.getRecipeLevel().getValue() : null)
                .ingredients(ingredients)
                .steps(steps)
                .build();
    }

    public void rateRecipe(Integer userId, Integer recipeId, RecipeRatingRequest request) {
        User user = getUserById(userId);
        Recipe recipe = getRecipeById(recipeId);

        RecipeRating rating = recipeRatingRepository.findByUserAndRecipe(user, recipe)
                .orElse(RecipeRating.builder()
                        .user(user)
                        .recipe(recipe)
                        .build());

        rating.updateRating(request.getRating());
        recipeRatingRepository.save(rating);

        log.info("Recipe rated: {} by user: {} with rating: {}", recipeId, userId, request.getRating());
    }

    @Transactional(readOnly = true)
    public PagedResponse<FavoriteRecipeResponse> getFavoriteRecipes(Integer userId, int page, int size) {
        User user = getUserById(userId);
        Pageable pageable = PageRequest.of(page, size);

        Page<FavoriteRecipe> favoritePage = favoriteRecipeRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        List<FavoriteRecipeResponse> recipes = favoritePage.getContent().stream()
                .map(fr -> FavoriteRecipeResponse.builder()
                        .recipeId(fr.getRecipe().getId())
                        .name(fr.getRecipe().getName())
                        .imageUrl(fr.getRecipe().getImage())
                        .difficulty(fr.getRecipe().getRecipeLevel() != null ?
                                fr.getRecipe().getRecipeLevel().getValue() : null)
                        .build())
                .collect(Collectors.toList());

        return PagedResponse.of(recipes, (int) favoritePage.getTotalElements(),
                page, favoritePage.getTotalPages());
    }

    public void addFavoriteRecipe(Integer userId, Integer recipeId) {
        User user = getUserById(userId);
        Recipe recipe = getRecipeById(recipeId);

        if (!favoriteRecipeRepository.existsByUserAndRecipe(user, recipe)) {
            FavoriteRecipe favoriteRecipe = FavoriteRecipe.builder()
                    .user(user)
                    .recipe(recipe)
                    .build();

            favoriteRecipeRepository.save(favoriteRecipe);
            log.info("Recipe added to favorites: {} by user: {}", recipeId, userId);
        }
    }

    public void removeFavoriteRecipe(Integer userId, Integer recipeId) {
        User user = getUserById(userId);
        Recipe recipe = getRecipeById(recipeId);

        favoriteRecipeRepository.deleteByUserAndRecipe(user, recipe);
        log.info("Recipe removed from favorites: {} by user: {}", recipeId, userId);
    }

    public void completeCooking(Integer userId, CookingCompleteRequest request) {
        User user = getUserById(userId);
        Recipe recipe = getRecipeById(request.getRecipeId());

        // 요리 히스토리 생성
        CookingHistory cookingHistory = CookingHistory.builder()
                .user(user)
                .recipe(recipe)
                .build();

        CookingHistory savedHistory = cookingHistoryRepository.save(cookingHistory);

        // 사용된 식재료들을 CONSUMED 상태로 변경
        IngredientState consumedState = ingredientStateRepository.findByValue(IngredientState.Type.CONSUMED.getValue())
                .orElseThrow(() -> new ResourceNotFoundException("CONSUMED 상태를 찾을 수 없습니다."));

        for (CookingCompleteRequest.UsedIngredientInfo usedIngredientInfo : request.getUsedIngredients()) {
            Ingredient ingredient = getIngredientById(usedIngredientInfo.getIngredientId());

            // 사용자가 해당 식재료에 접근할 수 있는지 확인
            validateIngredientAccess(ingredient, user);

            ingredient.changeState(consumedState);

            // UsedIngredient 기록 생성
            UsedIngredient usedIngredient = UsedIngredient.builder()
                    .cookingHistory(savedHistory)
                    .ingredient(ingredient)
                    .build();

            savedHistory.getUsedIngredients().add(usedIngredient);
        }

        log.info("Cooking completed: recipe={} by user={}, used ingredients={}",
                request.getRecipeId(), userId, request.getUsedIngredients().size());
    }

    @Transactional(readOnly = true)
    public PagedResponse<CookingHistoryResponse> getCookingHistories(Integer userId, int page, int size) {
        User user = getUserById(userId);
        Pageable pageable = PageRequest.of(page, size);

        Page<CookingHistory> historyPage = cookingHistoryRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        List<CookingHistoryResponse> histories = historyPage.getContent().stream()
                .map(ch -> CookingHistoryResponse.builder()
                        .historyId(ch.getId())
                        .recipe(CookingHistoryResponse.RecipeInfo.builder()
                                .recipeId(ch.getRecipe().getId())
                                .name(ch.getRecipe().getName())
                                .imageUrl(ch.getRecipe().getImage())
                                .build())
                        .cookedAt(ch.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return PagedResponse.of(histories, (int) historyPage.getTotalElements(),
                page, historyPage.getTotalPages());
    }

    private List<String> getAvailableIngredientNames(Fridge fridge) {
        return ingredientRepository.findByFridgeAndIsDeletedFalse(fridge, Pageable.unpaged())
                .getContent()
                .stream()
                .filter(ingredient -> {
                    // FRESH 또는 NEAR_EXPIRY 상태의 식재료만 사용 가능
                    String stateValue = ingredient.getIngredientState().getValue();
                    return IngredientState.Type.FRESH.getValue().equals(stateValue) ||
                           IngredientState.Type.NEAR_EXPIRY.getValue().equals(stateValue);
                })
                .map(Ingredient::getName)
                .distinct()
                .collect(Collectors.toList());
    }

    private RecipeResponse mapToRecipeResponse(Recipe recipe) {
        List<String> requiredIngredients = recipe.getRecipeIngredients().stream()
                .map(RecipeIngredient::getName)
                .collect(Collectors.toList());

        return RecipeResponse.builder()
                .recipeId(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .imageUrl(recipe.getImage())
                .difficulty(recipe.getRecipeLevel() != null ? recipe.getRecipeLevel().getValue() : null)
                .requiredIngredients(requiredIngredients)
                .missingIngredientsCount(0) // 기본 목록에서는 0
                .build();
    }

    private RecipeResponse mapToRecipeResponseWithMissingIngredients(Recipe recipe, List<String> availableIngredients) {
        List<String> requiredIngredients = recipe.getRecipeIngredients().stream()
                .map(RecipeIngredient::getName)
                .collect(Collectors.toList());

        int missingCount = (int) requiredIngredients.stream()
                .filter(ingredient -> !availableIngredients.contains(ingredient))
                .count();

        return RecipeResponse.builder()
                .recipeId(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .imageUrl(recipe.getImage())
                .difficulty(recipe.getRecipeLevel() != null ? recipe.getRecipeLevel().getValue() : null)
                .requiredIngredients(requiredIngredients)
                .missingIngredientsCount(missingCount)
                .build();
    }

    private User getUserById(Integer userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Recipe getRecipeById(Integer recipeId) {
        return recipeRepository.findByIdAndIsValidTrue(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("레시피를 찾을 수 없습니다."));
    }

    private Fridge getFridgeById(Integer fridgeId) {
        return fridgeRepository.findByIdAndIsDeletedFalse(fridgeId)
                .orElseThrow(() -> new ResourceNotFoundException("냉장고를 찾을 수 없습니다."));
    }

    private Ingredient getIngredientById(Integer ingredientId) {
        return ingredientRepository.findByIdAndIsDeletedFalse(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("식재료를 찾을 수 없습니다."));
    }

    private void validateFridgeAccess(Fridge fridge, User user) {
        if (!fridge.isAccessibleBy(user)) {
            throw new BusinessException("냉장고에 접근할 권한이 없습니다.");
        }
    }

    private void validateIngredientAccess(Ingredient ingredient, User user) {
        Fridge fridge = ingredient.getCompartment().getFridge();
        if (!fridge.isAccessibleBy(user)) {
            throw new BusinessException("식재료에 접근할 권한이 없습니다.");
        }
    }
}