package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.Recipe;
import com.no1.recipick.user.api.domain.entity.RecipeRating;
import com.no1.recipick.user.api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRatingRepository extends JpaRepository<RecipeRating, Integer> {

    Optional<RecipeRating> findByUserAndRecipe(User user, Recipe recipe);

    boolean existsByUserAndRecipe(User user, Recipe recipe);

    @Query("SELECT AVG(r.rating) FROM RecipeRating r WHERE r.user = :user")
    Double findAverageRatingByUser(@Param("user") User user);

    long countByUser(User user);

    @Query("SELECT r.rating, COUNT(r) FROM RecipeRating r WHERE r.user = :user GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> findRatingDistributionByUser(@Param("user") User user);
}