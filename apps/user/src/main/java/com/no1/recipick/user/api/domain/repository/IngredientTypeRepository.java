package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.IngredientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientTypeRepository extends JpaRepository<IngredientType, Integer> {

    Optional<IngredientType> findByValue(String value);
}