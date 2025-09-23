package com.no1.recipick.user.api.service;

import com.no1.recipick.user.api.domain.entity.Ingredient;
import com.no1.recipick.user.api.domain.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpirationSchedulerService {

    private final IngredientRepository ingredientRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시 실행
    @Transactional
    public void checkExpiringIngredients() {
        log.info("Starting expiration check for ingredients");

        LocalDateTime threeDaysLater = LocalDateTime.now().plusDays(3);
        LocalDateTime today = LocalDateTime.now();

        // 유통기한이 3일 이내인 식재료 조회 (경고)
        List<Ingredient> warningIngredients = ingredientRepository
                .findIngredientsExpiringBetween(today, threeDaysLater);

        for (Ingredient ingredient : warningIngredients) {
            if (ingredient.getExpirationDate() != null) {
                int daysLeft = (int) ChronoUnit.DAYS.between(today.toLocalDate(),
                        ingredient.getExpirationDate().toLocalDate());
                if (daysLeft >= 1) {
                    notificationService.createExpirationWarning(
                            ingredient.getCompartment().getFridge().getOwner().getId(),
                            ingredient.getName(),
                            daysLeft
                    );
                    log.info("Expiration warning sent for ingredient: {} (expires in {} days)",
                            ingredient.getName(), daysLeft);
                }
            }
        }

        // 만료된 식재료에 대한 알림은 isExpired() 메서드 활용
        List<Ingredient> allIngredients = ingredientRepository.findAll();
        int alertCount = 0;

        for (Ingredient ingredient : allIngredients) {
            if (ingredient.isExpired() && !ingredient.getIsDeleted()) {
                notificationService.createExpirationAlert(
                        ingredient.getCompartment().getFridge().getOwner().getId(),
                        ingredient.getName()
                );
                alertCount++;
                log.info("Expiration alert sent for ingredient: {}", ingredient.getName());
            }
        }

        log.info("Expiration check completed. Warnings: {}, Alerts: {}",
                warningIngredients.size(), alertCount);
    }

    @Scheduled(cron = "0 0 18 * * *") // 매일 오후 6시 실행
    @Transactional(readOnly = true)
    public void generateRecipeRecommendations() {
        log.info("Starting daily recipe recommendation generation");

        // 유통기한이 3일 이내인 식재료가 있는 사용자들에게 레시피 추천
        LocalDateTime threeDaysLater = LocalDateTime.now().plusDays(3);
        LocalDateTime today = LocalDateTime.now();

        List<Ingredient> soonExpiringIngredients = ingredientRepository
                .findIngredientsExpiringBetween(today, threeDaysLater);

        soonExpiringIngredients.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        i -> i.getCompartment().getFridge().getOwner().getId()))
                .forEach((userId, ingredients) -> {
                    // 가장 빨리 만료되는 식재료 기준으로 추천
                    String ingredientName = ingredients.get(0).getName();
                    String recipeName = generateRecipeSuggestion(ingredientName);

                    notificationService.createRecipeRecommendation(userId, recipeName);
                    log.info("Recipe recommendation sent to user {} for ingredient: {}",
                            userId, ingredientName);
                });

        log.info("Recipe recommendation generation completed");
    }

    private String generateRecipeSuggestion(String ingredientName) {
        // 간단한 레시피 추천 로직 (실제로는 더 복잡한 알고리즘 사용)
        if (ingredientName.contains("토마토")) {
            return "토마토 파스타";
        } else if (ingredientName.contains("양파")) {
            return "양파 볶음";
        } else if (ingredientName.contains("계란")) {
            return "계란 볶음밥";
        } else if (ingredientName.contains("당근")) {
            return "당근 볶음";
        } else if (ingredientName.contains("감자")) {
            return "감자 조림";
        } else {
            return ingredientName + " 요리";
        }
    }
}