package com.no1.recipick.user.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.no1.recipick.user.api.dto.ApiResponse;
import com.no1.recipick.user.api.dto.response.NotificationCountResponse;
import com.no1.recipick.user.api.dto.response.NotificationResponse;
import com.no1.recipick.user.api.dto.response.PagedResponse;
import com.no1.recipick.user.api.domain.entity.NotificationType;
import com.no1.recipick.user.api.security.CustomUserPrincipal;
import com.no1.recipick.user.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "알림", description = "알림 관리 관련 API")
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/users/me/notifications")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) NotificationType type) {
        PagedResponse<NotificationResponse> result = notificationService.getNotifications(
                userPrincipal.getId(), page, size, type);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/users/me/notifications/count")
    public ResponseEntity<ApiResponse<NotificationCountResponse>> getUnreadCount(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        NotificationCountResponse result = notificationService.getUnreadCount(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/users/me/notifications/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer notificationId) {
        notificationService.markAsRead(userPrincipal.getId(), notificationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/me/notifications/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        notificationService.markAllAsRead(userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }
}