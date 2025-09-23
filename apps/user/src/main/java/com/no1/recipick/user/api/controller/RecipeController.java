package com.no1.recipick.user.api.controller;

import com.no1.recipick.user.api.dto.ApiResponse;
import com.no1.recipick.user.api.dto.request.CookingCompleteRequest;
import com.no1.recipick.user.api.dto.request.RecipeRatingRequest;
import com.no1.recipick.user.api.dto.response.*;
import com.no1.recipick.user.api.security.CustomUserPrincipal;
import com.no1.recipick.user.api.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "레시피", description = "레시피 관리 관련 API")
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "레시피 목록 조회", description = "페이지네이션과 난이도 필터로 레시피 목록을 조회합니다.")
    @GetMapping("/recipes")
    public ResponseEntity<ApiResponse<PagedResponse<RecipeResponse>>> getRecipes(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "난이도 필터") @RequestParam(required = false) String difficulty) {
        PagedResponse<RecipeResponse> result = recipeService.getRecipes(page, size, difficulty);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "추천 레시피 조회", description = "사용자의 냉장고 식재료를 기반으로 추천 레시피를 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/recipes/recommendations")
    public ResponseEntity<ApiResponse<PagedResponse<RecipeResponse>>> getRecommendedRecipes(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "난이도 필터") @RequestParam(required = false) String difficulty,
            @Parameter(description = "냉장고 ID (특정 냉장고 기준)") @RequestParam(required = false) Integer fridgeId) {
        PagedResponse<RecipeResponse> result = recipeService.getRecommendedRecipes(
                userPrincipal.getId(), page, size, difficulty, fridgeId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/recipes/{recipeId}")
    public ResponseEntity<ApiResponse<RecipeDetailResponse>> getRecipeDetail(
            @PathVariable Integer recipeId) {
        RecipeDetailResponse recipeDetail = recipeService.getRecipeDetail(recipeId);
        return ResponseEntity.ok(ApiResponse.success(recipeDetail));
    }

    @PostMapping("/recipes/{recipeId}/ratings")
    public ResponseEntity<ApiResponse<Void>> rateRecipe(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer recipeId,
            @Valid @RequestBody RecipeRatingRequest request) {
        recipeService.rateRecipe(userPrincipal.getId(), recipeId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/me/favorite-recipes")
    public ResponseEntity<ApiResponse<PagedResponse<FavoriteRecipeResponse>>> getFavoriteRecipes(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<FavoriteRecipeResponse> result = recipeService.getFavoriteRecipes(
                userPrincipal.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/users/me/favorite-recipes/{recipeId}")
    public ResponseEntity<ApiResponse<Void>> addFavoriteRecipe(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer recipeId) {
        recipeService.addFavoriteRecipe(userPrincipal.getId(), recipeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/me/favorite-recipes/{recipeId}")
    public ResponseEntity<ApiResponse<Void>> removeFavoriteRecipe(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer recipeId) {
        recipeService.removeFavoriteRecipe(userPrincipal.getId(), recipeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/me/cooking-histories")
    public ResponseEntity<ApiResponse<Void>> completeCooking(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @Valid @RequestBody CookingCompleteRequest request) {
        recipeService.completeCooking(userPrincipal.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/me/cooking-histories")
    public ResponseEntity<ApiResponse<PagedResponse<CookingHistoryResponse>>> getCookingHistories(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<CookingHistoryResponse> result = recipeService.getCookingHistories(
                userPrincipal.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}