package com.no1.recipick.user.api.controller;

import com.no1.recipick.user.api.dto.ApiResponse;
import com.no1.recipick.user.api.dto.request.TokenRefreshRequest;
import com.no1.recipick.user.api.dto.request.UserInitialRequest;
import com.no1.recipick.user.api.dto.response.TokenResponse;
import com.no1.recipick.user.api.security.CustomUserPrincipal;
import com.no1.recipick.user.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증", description = "사용자 인증 관련 API")
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/auth/token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenResponse tokenResponse = authService.refreshAccessToken(request);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    @Operation(summary = "초기 사용자 정보 설정", description = "OAuth2 로그인 후 초기 사용자 정보를 설정합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/users/me/initial")
    public ResponseEntity<ApiResponse<Void>> setupInitialUserInfo(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @Valid @RequestBody UserInitialRequest request) {
        authService.setupInitialUserInfo(userPrincipal.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        authService.logout(userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }
}