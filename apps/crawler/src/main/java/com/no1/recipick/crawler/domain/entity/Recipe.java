package com.no1.recipick.crawler.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recipes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "search_identity", nullable = false, unique = true, length = 10)
    private String searchIdentity;

    @Column(name = "is_valid", nullable = false)
    private Boolean isValid;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "image", length = 500)
    private String image;

    @Column(name = "serving", length = 50)
    private String serving;

    @Column(name = "time", length = 50)
    private String time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_level_id")
    private RecipeLevel recipeLevel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeStep> recipeSteps;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients;

    public void updateRecipeData(String name, String description, String image,
                                String serving, String time, RecipeLevel recipeLevel) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.serving = serving;
        this.time = time;
        this.recipeLevel = recipeLevel;
        this.isValid = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsInvalid() {
        this.isValid = false;
        this.updatedAt = LocalDateTime.now();
    }
}