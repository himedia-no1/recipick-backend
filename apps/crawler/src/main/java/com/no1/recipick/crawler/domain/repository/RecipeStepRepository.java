package com.no1.recipick.crawler.domain.repository;

import com.no1.recipick.crawler.domain.entity.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeStepRepository extends JpaRepository<RecipeStep, Integer> {
}