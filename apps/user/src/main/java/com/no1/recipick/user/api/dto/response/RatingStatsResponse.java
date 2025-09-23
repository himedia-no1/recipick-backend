package com.no1.recipick.user.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingStatsResponse {
    private Double averageRating;
    private Long totalRatings;
    private Map<Integer, Long> ratingDistribution; // rating -> count
}