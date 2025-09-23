package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.Fridge;
import com.no1.recipick.user.api.domain.entity.FridgeMember;
import com.no1.recipick.user.api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FridgeMemberRepository extends JpaRepository<FridgeMember, Integer> {

    Optional<FridgeMember> findByFridgeAndUserAndIsDeletedFalse(Fridge fridge, User user);

    List<FridgeMember> findByFridgeAndIsDeletedFalseOrderByCreatedAtAsc(Fridge fridge);

    List<FridgeMember> findByUserAndIsDeletedFalseOrderByCreatedAtDesc(User user);

    @Query("SELECT fm FROM FridgeMember fm " +
           "WHERE fm.user = :user AND fm.isFavorite = true " +
           "AND fm.isDeleted = false " +
           "ORDER BY fm.createdAt DESC")
    List<FridgeMember> findFavoritesByUser(@Param("user") User user);

    boolean existsByFridgeAndUserAndIsDeletedFalse(Fridge fridge, User user);

    long countByFridgeAndIsDeletedFalse(Fridge fridge);
}