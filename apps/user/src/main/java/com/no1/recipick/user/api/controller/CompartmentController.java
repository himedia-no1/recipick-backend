package com.no1.recipick.user.api.controller;

import com.no1.recipick.user.api.dto.ApiResponse;
import com.no1.recipick.user.api.dto.request.CompartmentCreateRequest;
import com.no1.recipick.user.api.dto.request.CompartmentUpdateRequest;
import com.no1.recipick.user.api.dto.response.CompartmentResponse;
import com.no1.recipick.user.api.security.CustomUserPrincipal;
import com.no1.recipick.user.api.service.CompartmentService;
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
@RequestMapping("/api/fridges/{fridgeId}/compartments")
@RequiredArgsConstructor
public class CompartmentController {

    private final CompartmentService compartmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Integer>>> createCompartment(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer fridgeId,
            @Valid @RequestBody CompartmentCreateRequest request) {
        Integer compartmentId = compartmentService.createCompartment(userPrincipal.getId(), fridgeId, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(Map.of("compartmentId", compartmentId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<CompartmentResponse>>>> getCompartments(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer fridgeId) {
        List<CompartmentResponse> compartments = compartmentService.getCompartments(userPrincipal.getId(), fridgeId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("compartments", compartments)));
    }

    @PatchMapping("/{compartmentId}")
    public ResponseEntity<ApiResponse<Void>> updateCompartment(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer fridgeId,
            @PathVariable Integer compartmentId,
            @Valid @RequestBody CompartmentUpdateRequest request) {
        compartmentService.updateCompartment(userPrincipal.getId(), fridgeId, compartmentId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{compartmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteCompartment(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Integer fridgeId,
            @PathVariable Integer compartmentId) {
        compartmentService.deleteCompartment(userPrincipal.getId(), fridgeId, compartmentId);
        return ResponseEntity.noContent().build();
    }
}