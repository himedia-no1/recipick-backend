package com.no1.recipick.crawler.domain.repository;

import com.no1.recipick.crawler.domain.entity.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, Integer> {

    Optional<IngredientCategory> findByValue(String value);

    List<IngredientCategory> findByValueStartingWith(String prefix);

    List<IngredientCategory> findByValueEndingWith(String suffix);

    List<IngredientCategory> findByValueContaining(String keyword);

    @Query(value = """
        SELECT id, value, optimized_korean_similarity(:ingredientName, value) as score
        FROM ingredient_categories
        WHERE
            value = :ingredientName
            OR value LIKE CONCAT(:ingredientName, '%')
            OR value LIKE CONCAT('%', :ingredientName)
            OR value LIKE CONCAT('%', :ingredientName, '%')
            OR :ingredientName LIKE CONCAT('%', value, '%')
            OR (ABS(LENGTH(value) - LENGTH(:ingredientName)) <= 3 AND levenshtein(:ingredientName, value) <= 2)
        ORDER BY score DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<Object[]> findBestMatchByName(@Param("ingredientName") String ingredientName,
                                           @Param("threshold") double threshold);

    @Query("SELECT ic FROM IngredientCategory ic ORDER BY ic.id")
    List<IngredientCategory> findAllOrderById();
}