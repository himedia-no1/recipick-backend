package com.no1.recipick.user.api.controller;

import com.no1.recipick.user.api.dto.ApiResponse;
import com.no1.recipick.user.api.dto.request.IngredientCreateRequest;
import com.no1.recipick.user.api.dto.request.IngredientStateChangeRequest;
import com.no1.recipick.user.api.dto.request.IngredientUpdateRequest;
import com.no1.recipick.user.api.dto.response.*;
import com.no1.recipick.user.api.security.CustomUserPrincipal;
import com.no1.recipick.user.api.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping("/ingredients/categories")
    public ResponseEntity<ApiResponse<Map<String, List<IngredientCategoryResponse>>>> getIngredientCategories() {
        List<IngredientCategoryResponse> categories = ingredientService.getIngredientCategories();
        return ResponseEntity.ok(ApiResponse.success(Map.of("categories", categories)));
    }

    @GetMapping("/ingredients/search")
    public ResponseEntity<ApiResponse<PagedResponse<IngredientSearchResponse>>> searchIngredients(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<IngredientSearchResponse> result = ingredientService.searchIngredients(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/compartments/{compartmentId}/ingredients")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> createIngredient(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer compartmentId,
            @Valid @RequestBody IngredientCreateRequest request) {
        Integer ingredientId = ingredientService.createIngredient(userPrincipal.getId(), compartmentId, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(Map.of("ingredientId", ingredientId)));
    }

    @GetMapping("/fridges/{fridgeId}/ingredients")
    public ResponseEntity<ApiResponse<PagedResponse<IngredientResponse>>> getFridgeIngredients(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer fridgeId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer state) {
        PagedResponse<IngredientResponse> result = ingredientService.getFridgeIngredients(
                userPrincipal.getId(), fridgeId, page, size, sort, state);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/ingredients/{ingredientId}")
    public ResponseEntity<ApiResponse<IngredientDetailResponse>> getIngredientDetail(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer ingredientId) {
        IngredientDetailResponse ingredientDetail = ingredientService.getIngredientDetail(
                userPrincipal.getId(), ingredientId);
        return ResponseEntity.ok(ApiResponse.success(ingredientDetail));
    }

    @PatchMapping("/ingredients/{ingredientId}")
    public ResponseEntity<ApiResponse<Void>> updateIngredient(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer ingredientId,
            @Valid @RequestBody IngredientUpdateRequest request) {
        ingredientService.updateIngredient(userPrincipal.getId(), ingredientId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/ingredients/{ingredientId}")
    public ResponseEntity<ApiResponse<Void>> deleteIngredient(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer ingredientId) {
        ingredientService.deleteIngredient(userPrincipal.getId(), ingredientId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/ingredients/{ingredientId}/state")
    public ResponseEntity<ApiResponse<Void>> changeIngredientState(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer ingredientId,
            @Valid @RequestBody IngredientStateChangeRequest request) {
        ingredientService.changeIngredientState(userPrincipal.getId(), ingredientId, request);
        return ResponseEntity.noContent().build();
    }
}