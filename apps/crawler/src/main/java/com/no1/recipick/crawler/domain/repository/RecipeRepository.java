package com.no1.recipick.crawler.domain.repository;

import com.no1.recipick.crawler.domain.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

    Page<Recipe> findByIsValidFalseOrderById(Pageable pageable);

    long countByIsValidFalse();
}