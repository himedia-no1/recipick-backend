package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.Fridge;
import com.no1.recipick.user.api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FridgeRepository extends JpaRepository<Fridge, Integer> {

    Optional<Fridge> findByIdAndIsDeletedFalse(Integer id);

    List<Fridge> findByOwnerAndIsDeletedFalseOrderByIsDefaultDescCreatedAtDesc(User owner);

    Optional<Fridge> findByOwnerAndIsDefaultTrueAndIsDeletedFalse(User owner);

    @Query("SELECT f FROM Fridge f " +
           "LEFT JOIN FridgeMember fm ON f.id = fm.fridge.id " +
           "WHERE (f.owner = :user OR (fm.user = :user AND fm.isDeleted = false)) " +
           "AND f.isDeleted = false " +
           "ORDER BY f.isDefault DESC, f.createdAt DESC")
    List<Fridge> findAccessibleFridgesByUser(@Param("user") User user);

    @Query("SELECT f FROM Fridge f " +
           "JOIN FridgeMember fm ON f.id = fm.fridge.id " +
           "WHERE fm.user = :user AND fm.isFavorite = true " +
           "AND f.isDeleted = false AND fm.isDeleted = false " +
           "ORDER BY f.createdAt DESC")
    List<Fridge> findFavoriteFridgesByUser(@Param("user") User user);

    long countByOwnerAndIsDeletedFalse(User owner);
}