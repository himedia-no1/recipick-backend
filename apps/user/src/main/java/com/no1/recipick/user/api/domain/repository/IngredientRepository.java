package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.*;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

    Optional<Ingredient> findByIdAndIsDeletedFalse(Integer id);

    List<Ingredient> findByCompartmentAndIsDeletedFalseOrderByCreatedAtDesc(Compartment compartment);

    @Query("SELECT i FROM Ingredient i " +
           "JOIN i.compartment c " +
           "WHERE c.fridge = :fridge AND i.isDeleted = false " +
           "ORDER BY i.createdAt DESC")
    Page<Ingredient> findByFridgeAndIsDeletedFalse(@Param("fridge") Fridge fridge, Pageable pageable);

    @Query("SELECT i FROM Ingredient i " +
           "JOIN i.compartment c " +
           "WHERE c.fridge = :fridge AND i.ingredientState = :state AND i.isDeleted = false " +
           "ORDER BY i.createdAt DESC")
    Page<Ingredient> findByFridgeAndStateAndIsDeletedFalse(@Param("fridge") Fridge fridge,
                                                          @Param("state") IngredientState state,
                                                          Pageable pageable);

    @Query("SELECT i FROM Ingredient i " +
           "WHERE i.name LIKE %:keyword% AND i.isDeleted = false " +
           "ORDER BY i.name ASC")
    Page<Ingredient> findByNameContainingAndIsDeletedFalse(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT i FROM Ingredient i " +
           "WHERE i.expirationDate IS NOT NULL " +
           "AND i.expirationDate BETWEEN :startDate AND :endDate " +
           "AND i.isDeleted = false " +
           "ORDER BY i.expirationDate ASC")
    List<Ingredient> findIngredientsExpiringBetween(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(i) FROM Ingredient i " +
           "JOIN i.compartment c " +
           "WHERE c.fridge = :fridge AND i.isDeleted = false")
    long countByFridgeAndIsDeletedFalse(@Param("fridge") Fridge fridge);

    @Query("SELECT COUNT(i) FROM Ingredient i " +
           "JOIN i.compartment c " +
           "JOIN c.fridge f " +
           "WHERE f.owner = :user AND i.isDeleted = false")
    Long countByFridge_OwnerAndIsDeletedFalse(@Param("user") User user);

}