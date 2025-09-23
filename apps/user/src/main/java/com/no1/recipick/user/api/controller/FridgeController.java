package com.no1.recipick.user.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.no1.recipick.user.api.dto.ApiResponse;
import com.no1.recipick.user.api.dto.request.FridgeCreateRequest;
import com.no1.recipick.user.api.dto.request.FridgeUpdateRequest;
import com.no1.recipick.user.api.dto.response.FridgeDetailResponse;
import com.no1.recipick.user.api.dto.response.FridgeResponse;
import com.no1.recipick.user.api.security.CustomUserPrincipal;
import com.no1.recipick.user.api.service.FridgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "냉장고", description = "냉장고 관리 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/fridges")
@RequiredArgsConstructor
public class FridgeController {

    private final FridgeService fridgeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Integer>>> createFridge(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @Valid @RequestBody FridgeCreateRequest request) {
        Integer fridgeId = fridgeService.createFridge(userPrincipal.getId(), request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(Map.of("fridgeId", fridgeId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<FridgeResponse>>>> getFridges(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestParam(required = false) Boolean isFavorite,
            @RequestParam(required = false) Boolean isDefault) {
        List<FridgeResponse> fridges = fridgeService.getFridges(userPrincipal.getId(), isFavorite, isDefault);
        return ResponseEntity.ok(ApiResponse.success(Map.of("fridges", fridges)));
    }

    @GetMapping("/{fridgeId}")
    public ResponseEntity<ApiResponse<FridgeDetailResponse>> getFridgeDetail(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer fridgeId) {
        FridgeDetailResponse fridgeDetail = fridgeService.getFridgeDetail(userPrincipal.getId(), fridgeId);
        return ResponseEntity.ok(ApiResponse.success(fridgeDetail));
    }

    @PatchMapping("/{fridgeId}")
    public ResponseEntity<ApiResponse<Void>> updateFridge(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer fridgeId,
            @Valid @RequestBody FridgeUpdateRequest request) {
        fridgeService.updateFridge(userPrincipal.getId(), fridgeId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{fridgeId}")
    public ResponseEntity<ApiResponse<Void>> deleteFridge(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer fridgeId) {
        fridgeService.deleteFridge(userPrincipal.getId(), fridgeId);
        return ResponseEntity.noContent().build();
    }
}