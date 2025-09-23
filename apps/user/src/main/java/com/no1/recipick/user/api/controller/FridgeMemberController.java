package com.no1.recipick.user.api.controller;

import com.no1.recipick.user.api.dto.ApiResponse;
import com.no1.recipick.user.api.dto.request.FridgeInviteRequest;
import com.no1.recipick.user.api.dto.request.InvitationResponseRequest;
import com.no1.recipick.user.api.dto.response.FridgeMemberResponse;
import com.no1.recipick.user.api.security.CustomUserPrincipal;
import com.no1.recipick.user.api.service.FridgeMemberService;
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
public class FridgeMemberController {

    private final FridgeMemberService fridgeMemberService;

    @PostMapping("/fridges/{fridgeId}/members")
    public ResponseEntity<ApiResponse<Void>> inviteMember(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer fridgeId,
            @Valid @RequestBody FridgeInviteRequest request) {
        fridgeMemberService.inviteMember(userPrincipal.getId(), fridgeId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fridges/{fridgeId}/members")
    public ResponseEntity<ApiResponse<Map<String, List<FridgeMemberResponse>>>> getFridgeMembers(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer fridgeId) {
        List<FridgeMemberResponse> members = fridgeMemberService.getFridgeMembers(userPrincipal.getId(), fridgeId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("members", members)));
    }

    @DeleteMapping("/fridges/{fridgeId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer fridgeId,
            @PathVariable Integer userId) {
        fridgeMemberService.removeMember(userPrincipal.getId(), fridgeId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/invitations/{invitationId}")
    public ResponseEntity<ApiResponse<Void>> respondToInvitation(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer invitationId,
            @Valid @RequestBody InvitationResponseRequest request) {
        fridgeMemberService.respondToInvitation(userPrincipal.getId(), invitationId, request);
        return ResponseEntity.noContent().build();
    }
}