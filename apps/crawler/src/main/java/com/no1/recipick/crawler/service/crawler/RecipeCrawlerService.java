package com.no1.recipick.crawler.service.crawler;

import com.no1.recipick.crawler.dto.CrawledRecipeDto;
import com.no1.recipick.crawler.dto.RecipeStepDto;
import com.no1.recipick.crawler.service.storage.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeCrawlerService {

    private final HtmlParsingService htmlParsingService;
    private final ImageStorageService imageStorageService;

    public CrawledRecipeDto crawlRecipe(String searchIdentity) {
        log.info("Starting recipe crawling for searchIdentity: {}", searchIdentity);

        try {
            // HTML 파싱
            CrawledRecipeDto crawledData = htmlParsingService.parseRecipe(searchIdentity);

            // 메인 이미지 업로드
            if (crawledData.getImageUrl() != null) {
                String uploadedImageUrl = imageStorageService.uploadImageFromUrl(crawledData.getImageUrl());
                crawledData.setImageUrl(uploadedImageUrl);
                log.debug("Main image uploaded to: {}", uploadedImageUrl);
            }

            // 조리단계 이미지들 업로드
            if (crawledData.getSteps() != null) {
                List<RecipeStepDto> updatedSteps = crawledData.getSteps().stream()
                        .map(this::uploadStepImage)
                        .collect(Collectors.toList());
                crawledData.setSteps(updatedSteps);
                log.debug("Uploaded {} step images", updatedSteps.size());
            }

            log.info("Recipe crawling completed for searchIdentity: {}", searchIdentity);
            return crawledData;

        } catch (Exception e) {
            log.error("Recipe crawling failed for searchIdentity: {}", searchIdentity, e);
            throw new RuntimeException("Recipe crawling failed", e);
        }
    }

    private RecipeStepDto uploadStepImage(RecipeStepDto step) {
        if (step.getImageUrl() != null && !step.getImageUrl().isEmpty()) {
            try {
                String uploadedUrl = imageStorageService.uploadImageFromUrl(step.getImageUrl());
                step.setImageUrl(uploadedUrl);
            } catch (Exception e) {
                log.warn("Failed to upload step image: {}", step.getImageUrl(), e);
                // 이미지 업로드 실패해도 단계는 유지
                step.setImageUrl(null);
            }
        }
        return step;
    }
}