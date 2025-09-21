package com.no1.recipick.crawler.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_steps")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "image", length = 500)
    private String image;

    public static RecipeStep of(Recipe recipe, String description, String image) {
        return RecipeStep.builder()
                .recipe(recipe)
                .description(description)
                .image(image)
                .build();
    }
}