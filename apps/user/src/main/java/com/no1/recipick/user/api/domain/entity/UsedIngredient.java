package com.no1.recipick.user.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "used_ingredients")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsedIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooking_history_id", nullable = false)
    private CookingHistory cookingHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;
}