package com.no1.recipick.user.api.service;

import com.no1.recipick.user.api.domain.entity.*;
import com.no1.recipick.user.api.domain.repository.*;
import com.no1.recipick.user.api.dto.request.CompartmentCreateRequest;
import com.no1.recipick.user.api.dto.request.CompartmentUpdateRequest;
import com.no1.recipick.user.api.dto.response.CompartmentResponse;
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
public class CompartmentService {

    private final CompartmentRepository compartmentRepository;
    private final FridgeRepository fridgeRepository;
    private final UserRepository userRepository;
    private final CompartmentTypeRepository compartmentTypeRepository;

    public Integer createCompartment(Integer userId, Integer fridgeId, CompartmentCreateRequest request) {
        User user = getUserById(userId);
        Fridge fridge = getFridgeById(fridgeId);

        validateFridgeOwnership(fridge, user);

        CompartmentType compartmentType = compartmentTypeRepository.findById(request.getCompartmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("보관실 타입을 찾을 수 없습니다."));

        Compartment compartment = Compartment.builder()
                .fridge(fridge)
                .compartmentType(compartmentType)
                .name(request.getName())
                .isDeleted(false)
                .build();

        Compartment savedCompartment = compartmentRepository.save(compartment);
        log.info("Compartment created: {} in fridge: {} by user: {}", savedCompartment.getId(), fridgeId, userId);

        return savedCompartment.getId();
    }

    @Transactional(readOnly = true)
    public List<CompartmentResponse> getCompartments(Integer userId, Integer fridgeId) {
        User user = getUserById(userId);
        Fridge fridge = getFridgeById(fridgeId);

        validateFridgeAccess(fridge, user);

        List<Compartment> compartments = compartmentRepository.findByFridgeAndIsDeletedFalseOrderByCreatedAtAsc(fridge);

        return compartments.stream()
                .map(this::mapToCompartmentResponse)
                .collect(Collectors.toList());
    }

    public void updateCompartment(Integer userId, Integer fridgeId, Integer compartmentId,
                                CompartmentUpdateRequest request) {
        User user = getUserById(userId);
        Fridge fridge = getFridgeById(fridgeId);
        Compartment compartment = getCompartmentById(compartmentId);

        validateFridgeOwnership(fridge, user);
        validateCompartmentBelongsToFridge(compartment, fridge);

        compartment.updateName(request.getName());
        log.info("Compartment updated: {} by user: {}", compartmentId, userId);
    }

    public void deleteCompartment(Integer userId, Integer fridgeId, Integer compartmentId) {
        User user = getUserById(userId);
        Fridge fridge = getFridgeById(fridgeId);
        Compartment compartment = getCompartmentById(compartmentId);

        validateFridgeOwnership(fridge, user);
        validateCompartmentBelongsToFridge(compartment, fridge);

        compartment.softDelete();
        log.info("Compartment deleted: {} by user: {}", compartmentId, userId);
    }

    private CompartmentResponse mapToCompartmentResponse(Compartment compartment) {
        return CompartmentResponse.builder()
                .compartmentId(compartment.getId())
                .compartmentTypeId(compartment.getCompartmentType().getId())
                .name(compartment.getName())
                .build();
    }

    private User getUserById(Integer userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Fridge getFridgeById(Integer fridgeId) {
        return fridgeRepository.findByIdAndIsDeletedFalse(fridgeId)
                .orElseThrow(() -> new ResourceNotFoundException("냉장고를 찾을 수 없습니다."));
    }

    private Compartment getCompartmentById(Integer compartmentId) {
        return compartmentRepository.findByIdAndIsDeletedFalse(compartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("보관칸을 찾을 수 없습니다."));
    }

    private void validateFridgeAccess(Fridge fridge, User user) {
        if (!fridge.isAccessibleBy(user)) {
            throw new BusinessException("냉장고에 접근할 권한이 없습니다.");
        }
    }

    private void validateFridgeOwnership(Fridge fridge, User user) {
        if (!fridge.isOwnedBy(user)) {
            throw new BusinessException("보관칸 관리 권한이 없습니다.");
        }
    }

    private void validateCompartmentBelongsToFridge(Compartment compartment, Fridge fridge) {
        if (!compartment.getFridge().getId().equals(fridge.getId())) {
            throw new BusinessException("해당 냉장고의 보관칸이 아닙니다.");
        }
    }
}