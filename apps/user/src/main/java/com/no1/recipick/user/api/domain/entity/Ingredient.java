package com.no1.recipick.user.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "ingredients")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compartment_id", nullable = false)
    private Compartment compartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_type_id", nullable = false)
    private IngredientType ingredientType;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_category_id")
    private IngredientCategory ingredientCategory;

    @Column(name = "memo", length = 500)
    private String memo;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_state_id", nullable = false)
    private IngredientState ingredientState;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    public void updateInfo(String name, String memo, LocalDateTime expirationDate) {
        if (name != null) {
            this.name = name;
        }
        if (memo != null) {
            this.memo = memo;
        }
        if (expirationDate != null) {
            this.expirationDate = expirationDate;
        }
    }

    public void changeState(IngredientState newState) {
        this.ingredientState = newState;
    }

    public void markAsConsumed(IngredientState consumedState) {
        this.ingredientState = consumedState;
    }

    public void markAsDisposed(IngredientState disposedState) {
        this.ingredientState = disposedState;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        if (expirationDate == null) return false;
        return LocalDateTime.now().isAfter(expirationDate);
    }

    public boolean isNearExpiry(int daysThreshold) {
        if (expirationDate == null) return false;
        return LocalDateTime.now().plusDays(daysThreshold).isAfter(expirationDate);
    }
}