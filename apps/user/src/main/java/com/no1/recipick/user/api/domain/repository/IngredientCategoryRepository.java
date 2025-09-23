package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, Integer> {

    Optional<IngredientCategory> findByValue(String value);
}