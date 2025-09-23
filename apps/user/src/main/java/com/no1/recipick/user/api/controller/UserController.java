package com.no1.recipick.user.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.no1.recipick.user.api.dto.ApiResponse;
import com.no1.recipick.user.api.dto.request.UserProfileUpdateRequest;
import com.no1.recipick.user.api.dto.response.*;
import com.no1.recipick.user.api.security.CustomUserPrincipal;
import com.no1.recipick.user.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자", description = "사용자 정보 및 통계 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        UserResponse userResponse = userService.getCurrentUser(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        userService.updateProfile(userPrincipal.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        userService.deleteUser(userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getUserStats(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        UserStatsResponse stats = userService.getUserStats(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/cooking-stats")
    public ResponseEntity<ApiResponse<CookingStatsResponse>> getCookingStats(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestParam(defaultValue = "30") int days) {
        CookingStatsResponse stats = userService.getCookingStats(userPrincipal.getId(), days);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/rating-stats")
    public ResponseEntity<ApiResponse<RatingStatsResponse>> getRatingStats(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        RatingStatsResponse stats = userService.getRatingStats(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}