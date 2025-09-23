package com.no1.recipick.user.api.domain.repository;

import com.no1.recipick.user.api.domain.entity.Fridge;
import com.no1.recipick.user.api.domain.entity.Invitation;
import com.no1.recipick.user.api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {

    Optional<Invitation> findByIdAndInvitee(Integer id, User invitee);

    List<Invitation> findByInviteeAndStatusOrderByCreatedAtDesc(User invitee, Invitation.Status status);

    List<Invitation> findByInviterAndStatusOrderByCreatedAtDesc(User inviter, Invitation.Status status);

    @Query("SELECT i FROM Invitation i " +
           "WHERE i.fridge = :fridge AND i.invitee = :invitee " +
           "AND i.status = :status")
    Optional<Invitation> findByFridgeAndInviteeAndStatus(@Param("fridge") Fridge fridge,
                                                        @Param("invitee") User invitee,
                                                        @Param("status") Invitation.Status status);

    boolean existsByFridgeAndInviteeAndStatus(Fridge fridge, User invitee, Invitation.Status status);
}