package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.CompartmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompartmentTypeRepository extends JpaRepository<CompartmentType, Integer> {

    Optional<CompartmentType> findByValue(String value);
}