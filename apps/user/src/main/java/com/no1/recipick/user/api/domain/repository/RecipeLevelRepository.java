package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.RecipeLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeLevelRepository extends JpaRepository<RecipeLevel, Integer> {

    Optional<RecipeLevel> findByValue(String value);
}