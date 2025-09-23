package com.no1.recipick.user.api.service;

import com.no1.recipick.user.api.domain.entity.*;
import com.no1.recipick.user.api.domain.repository.*;
import com.no1.recipick.user.api.dto.request.FridgeInviteRequest;
import com.no1.recipick.user.api.dto.request.InvitationResponseRequest;
import com.no1.recipick.user.api.dto.response.FridgeMemberResponse;
import com.no1.recipick.user.api.exception.BusinessException;
import com.no1.recipick.user.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FridgeMemberService {

    private final FridgeRepository fridgeRepository;
    private final UserRepository userRepository;
    private final FridgeMemberRepository fridgeMemberRepository;
    private final InvitationRepository invitationRepository;

    public void inviteMember(Integer ownerId, Integer fridgeId, FridgeInviteRequest request) {
        User owner = getUserById(ownerId);
        User invitee = getUserById(request.getUserId());
        Fridge fridge = getFridgeById(fridgeId);

        validateFridgeOwnership(fridge, owner);

        // 자신을 초대할 수 없음
        if (ownerId.equals(request.getUserId())) {
            throw new BusinessException("자신을 초대할 수 없습니다.");
        }

        // 이미 멤버인지 확인
        if (fridgeMemberRepository.existsByFridgeAndUserAndIsDeletedFalse(fridge, invitee)) {
            throw new BusinessException("이미 냉장고 멤버입니다.");
        }

        // 이미 대기 중인 초대가 있는지 확인
        if (invitationRepository.existsByFridgeAndInviteeAndStatus(fridge, invitee, Invitation.Status.PENDING)) {
            throw new BusinessException("이미 초대를 보냈습니다.");
        }

        Invitation invitation = Invitation.builder()
                .fridge(fridge)
                .inviter(owner)
                .invitee(invitee)
                .status(Invitation.Status.PENDING)
                .build();

        invitationRepository.save(invitation);
        log.info("Fridge invitation sent: fridge={}, inviter={}, invitee={}", fridgeId, ownerId, request.getUserId());
    }

    @Transactional(readOnly = true)
    public List<FridgeMemberResponse> getFridgeMembers(Integer userId, Integer fridgeId) {
        User user = getUserById(userId);
        Fridge fridge = getFridgeById(fridgeId);

        validateFridgeAccess(fridge, user);

        List<FridgeMemberResponse> members = new ArrayList<>();

        // 소유자 추가
        members.add(FridgeMemberResponse.builder()
                .userId(fridge.getOwner().getId())
                .nickname(fridge.getOwner().getNickname())
                .isOwner(true)
                .build());

        // 멤버들 추가
        List<FridgeMember> fridgeMembers = fridgeMemberRepository.findByFridgeAndIsDeletedFalseOrderByCreatedAtAsc(fridge);
        fridgeMembers.forEach(member -> members.add(FridgeMemberResponse.builder()
                .userId(member.getUser().getId())
                .nickname(member.getUser().getNickname())
                .isOwner(false)
                .build()));

        return members;
    }

    public void removeMember(Integer ownerId, Integer fridgeId, Integer memberId) {
        User owner = getUserById(ownerId);
        User member = getUserById(memberId);
        Fridge fridge = getFridgeById(fridgeId);

        validateFridgeOwnership(fridge, owner);

        // 소유자는 제거할 수 없음
        if (fridge.isOwnedBy(member)) {
            throw new BusinessException("냉장고 소유자는 제거할 수 없습니다.");
        }

        FridgeMember fridgeMember = fridgeMemberRepository.findByFridgeAndUserAndIsDeletedFalse(fridge, member)
                .orElseThrow(() -> new ResourceNotFoundException("냉장고 멤버를 찾을 수 없습니다."));

        fridgeMember.softDelete();
        log.info("Fridge member removed: fridge={}, owner={}, member={}", fridgeId, ownerId, memberId);
    }

    public void respondToInvitation(Integer inviteeId, Integer invitationId, InvitationResponseRequest request) {
        User invitee = getUserById(inviteeId);
        Invitation invitation = invitationRepository.findByIdAndInvitee(invitationId, invitee)
                .orElseThrow(() -> new ResourceNotFoundException("초대를 찾을 수 없습니다."));

        if (!invitation.isPending()) {
            throw new BusinessException("이미 처리된 초대입니다.");
        }

        if (request.getIsAccepted()) {
            invitation.accept();

            // FridgeMember 생성
            FridgeMember fridgeMember = FridgeMember.builder()
                    .fridge(invitation.getFridge())
                    .user(invitee)
                    .isFavorite(false)
                    .isDeleted(false)
                    .build();

            fridgeMemberRepository.save(fridgeMember);
            log.info("Invitation accepted: invitation={}, invitee={}", invitationId, inviteeId);
        } else {
            invitation.reject();
            log.info("Invitation rejected: invitation={}, invitee={}", invitationId, inviteeId);
        }
    }

    private User getUserById(Integer userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Fridge getFridgeById(Integer fridgeId) {
        return fridgeRepository.findByIdAndIsDeletedFalse(fridgeId)
                .orElseThrow(() -> new ResourceNotFoundException("냉장고를 찾을 수 없습니다."));
    }

    private void validateFridgeAccess(Fridge fridge, User user) {
        if (!fridge.isAccessibleBy(user)) {
            throw new BusinessException("냉장고에 접근할 권한이 없습니다.");
        }
    }

    private void validateFridgeOwnership(Fridge fridge, User user) {
        if (!fridge.isOwnedBy(user)) {
            throw new BusinessException("냉장고 관리 권한이 없습니다.");
        }
    }
}