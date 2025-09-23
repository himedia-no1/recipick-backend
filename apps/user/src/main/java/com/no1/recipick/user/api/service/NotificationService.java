package com.no1.recipick.user.api.service;

import com.no1.recipick.user.api.domain.entity.Notification;
import com.no1.recipick.user.api.domain.entity.NotificationType;
import com.no1.recipick.user.api.domain.entity.User;
import com.no1.recipick.user.api.domain.repository.NotificationRepository;
import com.no1.recipick.user.api.domain.repository.UserRepository;
import com.no1.recipick.user.api.dto.response.NotificationCountResponse;
import com.no1.recipick.user.api.dto.response.NotificationResponse;
import com.no1.recipick.user.api.dto.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public PagedResponse<NotificationResponse> getNotifications(Integer userId, int page, int size, NotificationType type) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications;

        if (type != null) {
            notifications = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        Page<NotificationResponse> responsePage = notifications.map(this::convertToResponse);

        return PagedResponse.of(
                responsePage.getContent(),
                (int) responsePage.getTotalElements(),
                responsePage.getNumber(),
                responsePage.getTotalPages()
        );
    }

    public NotificationCountResponse getUnreadCount(Integer userId) {
        Long unreadCount = notificationRepository.countUnreadNotifications(userId);
        return NotificationCountResponse.builder()
                .unreadCount(unreadCount)
                .build();
    }

    @Transactional
    public void markAsRead(Integer userId, Integer notificationId) {
        notificationRepository.markAsRead(notificationId, userId);
        log.info("Notification {} marked as read for user {}", notificationId, userId);
    }

    @Transactional
    public void markAllAsRead(Integer userId) {
        notificationRepository.markAllAsRead(userId);
        log.info("All notifications marked as read for user {}", userId);
    }

    @Transactional
    public void createNotification(Integer userId, NotificationType type, String title, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .build();

        notificationRepository.save(notification);
        log.info("Notification created for user {}: {}", userId, title);
    }

    @Transactional
    public void createExpirationWarning(Integer userId, String ingredientName, int daysLeft) {
        String title = "유통기한 임박 알림";
        String message = String.format("%s의 유통기한이 %d일 남았습니다.", ingredientName, daysLeft);
        createNotification(userId, NotificationType.EXPIRATION_WARNING, title, message);
    }

    @Transactional
    public void createExpirationAlert(Integer userId, String ingredientName) {
        String title = "유통기한 만료 알림";
        String message = String.format("%s의 유통기한이 지났습니다. 확인해주세요.", ingredientName);
        createNotification(userId, NotificationType.EXPIRATION_ALERT, title, message);
    }

    @Transactional
    public void createRecipeRecommendation(Integer userId, String recipeName) {
        String title = "추천 레시피";
        String message = String.format("보유한 식재료로 '%s'를 만들어보세요!", recipeName);
        createNotification(userId, NotificationType.RECIPE_RECOMMENDATION, title, message);
    }

    @Transactional
    public void createFridgeShareNotification(Integer userId, String fridgeName, String ownerName) {
        String title = "냉장고 공유 알림";
        String message = String.format("%s님이 '%s' 냉장고를 공유했습니다.", ownerName, fridgeName);
        createNotification(userId, NotificationType.FRIDGE_SHARE, title, message);
    }

    private NotificationResponse convertToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}