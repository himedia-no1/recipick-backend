package com.no1.recipick.user.api.service;

import com.no1.recipick.user.api.domain.entity.User;
import com.no1.recipick.user.api.domain.repository.*;
import com.no1.recipick.user.api.dto.request.UserProfileUpdateRequest;
import com.no1.recipick.user.api.dto.response.*;
import com.no1.recipick.user.api.exception.BusinessException;
import com.no1.recipick.user.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final CookingHistoryRepository cookingHistoryRepository;
    private final FavoriteRecipeRepository favoriteRecipeRepository;
    private final FridgeRepository fridgeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeRatingRepository recipeRatingRepository;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Integer userId) {
        User user = getUserById(userId);

        return UserResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .build();
    }

    public void updateProfile(Integer userId, UserProfileUpdateRequest request) {
        User user = getUserById(userId);

        // 닉네임 중복 검사
        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNicknameAndIsDeletedFalse(request.getNickname())) {
                throw new BusinessException("이미 존재하는 닉네임입니다.");
            }
        }

        user.updateProfile(request.getNickname(), request.getProfileImage());
        log.info("User profile updated: {}", userId);
    }

    public void deleteUser(Integer userId) {
        User user = getUserById(userId);

        user.softDelete();
        log.info("User deleted: {}", userId);
    }

    @Transactional(readOnly = true)
    public UserStatsResponse getUserStats(Integer userId) {
        User user = getUserById(userId);

        Long totalRecipesCooked = cookingHistoryRepository.countByUser(user);
        Long totalFavoriteRecipes = favoriteRecipeRepository.countByUser(user);
        Long totalFridges = fridgeRepository.countByOwnerAndIsDeletedFalse(user);
        Long totalIngredients = ingredientRepository.countByFridge_OwnerAndIsDeletedFalse(user);

        return UserStatsResponse.builder()
                .totalRecipesCooked(totalRecipesCooked)
                .totalFavoriteRecipes(totalFavoriteRecipes)
                .totalFridges(totalFridges)
                .totalIngredients(totalIngredients)
                .build();
    }

    @Transactional(readOnly = true)
    public CookingStatsResponse getCookingStats(Integer userId, int days) {
        User user = getUserById(userId);
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        Long totalCookingCount = cookingHistoryRepository.countByUser(user);

        // 일별 요리 통계
        List<Object[]> dailyStats = cookingHistoryRepository.findDailyCookingStats(user, startDate);
        Map<LocalDate, Long> dailyCookingCount = new HashMap<>();
        for (Object[] stat : dailyStats) {
            LocalDate date = ((java.sql.Date) stat[0]).toLocalDate();
            Long count = (Long) stat[1];
            dailyCookingCount.put(date, count);
        }

        // 월별 추세
        List<Object[]> monthlyStats = cookingHistoryRepository.findMonthlyCookingStats(user, startDate);
        Map<String, Long> monthlyTrend = new HashMap<>();
        for (Object[] stat : monthlyStats) {
            Integer year = (Integer) stat[0];
            Integer month = (Integer) stat[1];
            Long count = (Long) stat[2];
            String key = String.format("%d-%02d", year, month);
            monthlyTrend.put(key, count);
        }

        return CookingStatsResponse.builder()
                .totalCookingCount(totalCookingCount)
                .dailyCookingCount(dailyCookingCount)
                .monthlyTrend(monthlyTrend)
                .build();
    }

    @Transactional(readOnly = true)
    public RatingStatsResponse getRatingStats(Integer userId) {
        User user = getUserById(userId);

        Double averageRating = recipeRatingRepository.findAverageRatingByUser(user);
        Long totalRatings = recipeRatingRepository.countByUser(user);

        // 평점 분포
        List<Object[]> ratingDistData = recipeRatingRepository.findRatingDistributionByUser(user);
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (Object[] data : ratingDistData) {
            Integer rating = (Integer) data[0];
            Long count = (Long) data[1];
            ratingDistribution.put(rating, count);
        }

        return RatingStatsResponse.builder()
                .averageRating(averageRating != null ? averageRating : 0.0)
                .totalRatings(totalRatings)
                .ratingDistribution(ratingDistribution)
                .build();
    }

    private User getUserById(Integer userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }
}