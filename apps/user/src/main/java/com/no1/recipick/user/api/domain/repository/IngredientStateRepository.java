package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.IngredientState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientStateRepository extends JpaRepository<IngredientState, Integer> {

    Optional<IngredientState> findByValue(String value);
}