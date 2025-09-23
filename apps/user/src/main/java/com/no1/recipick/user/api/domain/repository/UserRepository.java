package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.CredentialType;
import com.no1.recipick.user.api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findBySocialIdentityAndCredentialTypeAndIsDeletedFalse(String socialIdentity, CredentialType credentialType);

    Optional<User> findByIdAndIsDeletedFalse(Integer id);

    Optional<User> findByNicknameAndIsDeletedFalse(String nickname);

    boolean existsByNicknameAndIsDeletedFalse(String nickname);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
    Optional<User> findByEmailAndIsDeletedFalse(@Param("email") String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isDeleted = false")
    long countActiveUsers();
}