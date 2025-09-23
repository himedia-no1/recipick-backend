package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.Compartment;
import com.no1.recipick.user.api.domain.entity.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompartmentRepository extends JpaRepository<Compartment, Integer> {

    Optional<Compartment> findByIdAndIsDeletedFalse(Integer id);

    List<Compartment> findByFridgeAndIsDeletedFalseOrderByCreatedAtAsc(Fridge fridge);

    long countByFridgeAndIsDeletedFalse(Fridge fridge);
}