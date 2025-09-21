package com.no1.recipick.crawler.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_ingredients")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_category_id")
    private IngredientCategory ingredientCategory;

    public static RecipeIngredient of(Recipe recipe, String name, IngredientCategory ingredientCategory) {
        return RecipeIngredient.builder()
                .recipe(recipe)
                .name(name)
                .ingredientCategory(ingredientCategory)
                .build();
    }
}