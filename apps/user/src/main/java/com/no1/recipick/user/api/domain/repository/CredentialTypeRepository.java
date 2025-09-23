package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.CredentialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialTypeRepository extends JpaRepository<CredentialType, Integer> {

    Optional<CredentialType> findByValue(String value);
}