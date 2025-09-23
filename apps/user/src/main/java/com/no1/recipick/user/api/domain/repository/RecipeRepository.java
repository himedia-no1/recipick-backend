package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.Recipe;
import com.no1.recipick.user.api.domain.entity.RecipeLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

    Optional<Recipe> findByIdAndIsValidTrue(Integer id);

    Page<Recipe> findByIsValidTrueOrderByIdDesc(Pageable pageable);

    Page<Recipe> findByRecipeLevelAndIsValidTrueOrderByIdDesc(RecipeLevel recipeLevel, Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE r.isValid = true AND " +
           "r.id IN (SELECT ri.recipe.id FROM RecipeIngredient ri WHERE ri.name IN :ingredientNames) " +
           "ORDER BY r.id DESC")
    Page<Recipe> findRecommendedRecipes(@Param("ingredientNames") List<String> ingredientNames, Pageable pageable);

    @Query("SELECT r FROM Recipe r " +
           "JOIN r.recipeIngredients ri " +
           "WHERE r.isValid = true AND ri.name IN :availableIngredients " +
           "GROUP BY r.id " +
           "ORDER BY COUNT(ri.id) DESC, r.id DESC")
    Page<Recipe> findRecipesByAvailableIngredients(@Param("availableIngredients") List<String> availableIngredients,
                                                   Pageable pageable);
}