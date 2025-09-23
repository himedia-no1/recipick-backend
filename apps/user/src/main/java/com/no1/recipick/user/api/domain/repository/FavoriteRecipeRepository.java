package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.FavoriteRecipe;
import com.no1.recipick.user.api.domain.entity.Recipe;
import com.no1.recipick.user.api.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRecipeRepository extends JpaRepository<FavoriteRecipe, Integer> {

    Optional<FavoriteRecipe> findByUserAndRecipe(User user, Recipe recipe);

    boolean existsByUserAndRecipe(User user, Recipe recipe);

    @Query("SELECT fr FROM FavoriteRecipe fr JOIN FETCH fr.recipe r WHERE fr.user = :user ORDER BY fr.createdAt DESC")
    Page<FavoriteRecipe> findByUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);

    void deleteByUserAndRecipe(User user, Recipe recipe);

    long countByUser(User user);
}