package com.no1.recipick.user.api.service;

import com.no1.recipick.user.api.domain.entity.*;
import com.no1.recipick.user.api.domain.repository.*;
import com.no1.recipick.user.api.dto.request.FridgeCreateRequest;
import com.no1.recipick.user.api.dto.request.FridgeUpdateRequest;
import com.no1.recipick.user.api.dto.response.FridgeDetailResponse;
import com.no1.recipick.user.api.dto.response.FridgeResponse;
import com.no1.recipick.user.api.exception.BusinessException;
import com.no1.recipick.user.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FridgeService {

    private final FridgeRepository fridgeRepository;
    private final UserRepository userRepository;
    private final CompartmentTypeRepository compartmentTypeRepository;
    private final FridgeMemberRepository fridgeMemberRepository;

    public Integer createFridge(Integer userId, FridgeCreateRequest request) {
        User user = getUserById(userId);

        // 첫 번째 냉장고인 경우 기본 냉장고로 설정
        boolean isFirstFridge = fridgeRepository.countByOwnerAndIsDeletedFalse(user) == 0;

        Fridge fridge = Fridge.builder()
                .owner(user)
                .name(request.getName())
                .isDefault(isFirstFridge)
                .isDeleted(false)
                .build();

        Fridge savedFridge = fridgeRepository.save(fridge);

        // 기본 보관구역 생성 (냉동, 냉장, 실온)
        createDefaultCompartments(savedFridge);

        log.info("Fridge created: {} by user: {}", savedFridge.getId(), userId);
        return savedFridge.getId();
    }

    @Transactional(readOnly = true)
    public List<FridgeResponse> getFridges(Integer userId, Boolean isFavorite, Boolean isDefault) {
        User user = getUserById(userId);
        List<Fridge> fridges;

        if (Boolean.TRUE.equals(isFavorite)) {
            fridges = fridgeRepository.findFavoriteFridgesByUser(user);
        } else if (Boolean.TRUE.equals(isDefault)) {
            Fridge defaultFridge = fridgeRepository.findByOwnerAndIsDefaultTrueAndIsDeletedFalse(user)
                    .orElse(null);
            fridges = defaultFridge != null ? List.of(defaultFridge) : List.of();
        } else {
            fridges = fridgeRepository.findAccessibleFridgesByUser(user);
        }

        return fridges.stream()
                .map(fridge -> mapToFridgeResponse(fridge, user))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FridgeDetailResponse getFridgeDetail(Integer userId, Integer fridgeId) {
        User user = getUserById(userId);
        Fridge fridge = getFridgeById(fridgeId);

        validateFridgeAccess(fridge, user);

        List<FridgeMember> members = fridgeMemberRepository.findByFridgeAndIsDeletedFalseOrderByCreatedAtAsc(fridge);

        return FridgeDetailResponse.builder()
                .fridgeId(fridge.getId())
                .name(fridge.getName())
                .memo(fridge.getMemo())
                .isDefault(fridge.getIsDefault())
                .isFavorite(isFridgeFavorite(fridge, user))
                .owner(FridgeDetailResponse.UserSummaryResponse.builder()
                        .userId(fridge.getOwner().getId())
                        .nickname(fridge.getOwner().getNickname())
                        .build())
                .members(members.stream()
                        .map(member -> FridgeDetailResponse.UserSummaryResponse.builder()
                                .userId(member.getUser().getId())
                                .nickname(member.getUser().getNickname())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public void updateFridge(Integer userId, Integer fridgeId, FridgeUpdateRequest request) {
        User user = getUserById(userId);
        Fridge fridge = getFridgeById(fridgeId);

        validateFridgeOwnership(fridge, user);

        fridge.updateInfo(request.getName(), request.getMemo(), request.getIsFavorite());
        log.info("Fridge updated: {} by user: {}", fridgeId, userId);
    }

    public void deleteFridge(Integer userId, Integer fridgeId) {
        User user = getUserById(userId);
        Fridge fridge = getFridgeById(fridgeId);

        validateFridgeOwnership(fridge, user);

        fridge.softDelete();
        log.info("Fridge deleted: {} by user: {}", fridgeId, userId);
    }

    private void createDefaultCompartments(Fridge fridge) {
        CompartmentType.Type[] defaultTypes = {
                CompartmentType.Type.FREEZER,
                CompartmentType.Type.REFRIGERATOR,
                CompartmentType.Type.ROOM_TEMPERATURE
        };

        for (CompartmentType.Type type : defaultTypes) {
            CompartmentType compartmentType = compartmentTypeRepository.findByValue(type.getValue())
                    .orElseThrow(() -> new ResourceNotFoundException("보관실 타입을 찾을 수 없습니다: " + type.getValue()));

            Compartment compartment = Compartment.builder()
                    .fridge(fridge)
                    .compartmentType(compartmentType)
                    .name(getDefaultCompartmentName(type))
                    .isDeleted(false)
                    .build();

            fridge.getCompartments().add(compartment);
        }
    }

    private String getDefaultCompartmentName(CompartmentType.Type type) {
        return switch (type) {
            case FREEZER -> "냉동실";
            case REFRIGERATOR -> "냉장실";
            case ROOM_TEMPERATURE -> "실온보관";
        };
    }

    private FridgeResponse mapToFridgeResponse(Fridge fridge, User user) {
        return FridgeResponse.builder()
                .fridgeId(fridge.getId())
                .name(fridge.getName())
                .isDefault(fridge.getIsDefault())
                .isFavorite(isFridgeFavorite(fridge, user))
                .build();
    }

    private boolean isFridgeFavorite(Fridge fridge, User user) {
        if (fridge.isOwnedBy(user)) {
            return false; // 소유자는 즐겨찾기 개념이 없음
        }
        return fridgeMemberRepository.findByFridgeAndUserAndIsDeletedFalse(fridge, user)
                .map(FridgeMember::getIsFavorite)
                .orElse(false);
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
            throw new BusinessException("냉장고 수정 권한이 없습니다.");
        }
    }
}