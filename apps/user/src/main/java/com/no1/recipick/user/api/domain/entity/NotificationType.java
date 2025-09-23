package com.no1.recipick.user.api.domain.entity;

public enum NotificationType {
    EXPIRATION_WARNING,    // 유통기한 임박 경고
    EXPIRATION_ALERT,      // 유통기한 만료 알림
    RECIPE_RECOMMENDATION, // 레시피 추천
    FRIDGE_SHARE          // 냉장고 공유 알림
}