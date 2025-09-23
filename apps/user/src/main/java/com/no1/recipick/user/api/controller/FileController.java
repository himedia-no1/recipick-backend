package com.no1.recipick.user.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.no1.recipick.user.api.dto.ApiResponse;
import com.no1.recipick.user.api.dto.response.FileUploadResponse;
import com.no1.recipick.user.api.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "파일", description = "파일 업로드 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload/profile")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadProfileImage(
            @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileService.uploadFile(file, "profiles");

            FileUploadResponse response = FileUploadResponse.builder()
                    .fileName(file.getOriginalFilename())
                    .fileUrl(fileUrl)
                    .message("프로필 이미지가 성공적으로 업로드되었습니다.")
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (Exception e) {
            log.error("Profile image upload failed: {}", e.getMessage());
            FileUploadResponse errorResponse = FileUploadResponse.builder()
                    .message("파일 업로드에 실패했습니다: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.success(errorResponse));
        }
    }

    @PostMapping("/upload/recipe")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadRecipeImage(
            @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileService.uploadFile(file, "recipes");

            FileUploadResponse response = FileUploadResponse.builder()
                    .fileName(file.getOriginalFilename())
                    .fileUrl(fileUrl)
                    .message("레시피 이미지가 성공적으로 업로드되었습니다.")
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (Exception e) {
            log.error("Recipe image upload failed: {}", e.getMessage());
            FileUploadResponse errorResponse = FileUploadResponse.builder()
                    .message("파일 업로드에 실패했습니다: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.success(errorResponse));
        }
    }

    @PostMapping("/upload/ingredient")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadIngredientImage(
            @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileService.uploadFile(file, "ingredients");

            FileUploadResponse response = FileUploadResponse.builder()
                    .fileName(file.getOriginalFilename())
                    .fileUrl(fileUrl)
                    .message("식재료 이미지가 성공적으로 업로드되었습니다.")
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (Exception e) {
            log.error("Ingredient image upload failed: {}", e.getMessage());
            FileUploadResponse errorResponse = FileUploadResponse.builder()
                    .message("파일 업로드에 실패했습니다: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.success(errorResponse));
        }
    }
}