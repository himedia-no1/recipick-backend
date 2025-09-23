package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.CookingHistory;
import com.no1.recipick.user.api.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CookingHistoryRepository extends JpaRepository<CookingHistory, Integer> {

    @Query("SELECT ch FROM CookingHistory ch JOIN FETCH ch.recipe r WHERE ch.user = :user ORDER BY ch.createdAt DESC")
    Page<CookingHistory> findByUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);

    long countByUser(User user);

    @Query("SELECT DATE(ch.createdAt) as date, COUNT(ch) as count " +
           "FROM CookingHistory ch " +
           "WHERE ch.user = :user AND ch.createdAt >= :startDate " +
           "GROUP BY DATE(ch.createdAt) " +
           "ORDER BY DATE(ch.createdAt)")
    List<Object[]> findDailyCookingStats(@Param("user") User user, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT FUNCTION('YEAR', ch.createdAt) as year, FUNCTION('MONTH', ch.createdAt) as month, COUNT(ch) as count " +
           "FROM CookingHistory ch " +
           "WHERE ch.user = :user AND ch.createdAt >= :startDate " +
           "GROUP BY FUNCTION('YEAR', ch.createdAt), FUNCTION('MONTH', ch.createdAt) " +
           "ORDER BY FUNCTION('YEAR', ch.createdAt), FUNCTION('MONTH', ch.createdAt)")
    List<Object[]> findMonthlyCookingStats(@Param("user") User user, @Param("startDate") LocalDateTime startDate);
}