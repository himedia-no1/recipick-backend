package com.no1.recipick.user.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookingStatsResponse {
    private Long totalCookingCount;
    private Map<LocalDate, Long> dailyCookingCount;
    private Map<String, Long> monthlyTrend;
}