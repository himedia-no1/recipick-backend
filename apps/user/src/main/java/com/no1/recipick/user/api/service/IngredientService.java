package com.no1.recipick.user.api.service;

import com.no1.recipick.user.api.domain.entity.*;
import com.no1.recipick.user.api.domain.repository.*;
import com.no1.recipick.user.api.dto.request.IngredientCreateRequest;
import com.no1.recipick.user.api.dto.request.IngredientStateChangeRequest;
import com.no1.recipick.user.api.dto.request.IngredientUpdateRequest;
import com.no1.recipick.user.api.dto.response.*;
import com.no1.recipick.user.api.exception.BusinessException;
import com.no1.recipick.user.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final CompartmentRepository compartmentRepository;
    private final UserRepository userRepository;
    private final IngredientTypeRepository ingredientTypeRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final IngredientStateRepository ingredientStateRepository;

    @Transactional(readOnly = true)
    public List<IngredientCategoryResponse> getIngredientCategories() {
        List<IngredientCategory> categories = ingredientCategoryRepository.findAll();
        return categories.stream()
                .map(category -> IngredientCategoryResponse.builder()
                        .id(category.getId())
                        .value(category.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagedResponse<IngredientSearchResponse> searchIngredients(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Ingredient> ingredientPage = ingredientRepository.findByNameContainingAndIsDeletedFalse(keyword, pageable);

        List<IngredientSearchResponse> ingredients = ingredientPage.getContent().stream()
                .map(ingredient -> IngredientSearchResponse.builder()
                        .ingredientId(ingredient.getId())
                        .name(ingredient.getName())
                        .ingredientCategoryId(ingredient.getIngredientCategory() != null ?
                                ingredient.getIngredientCategory().getId() : null)
                        .build())
                .collect(Collectors.toList());

        return PagedResponse.of(ingredients, (int) ingredientPage.getTotalElements(),
                page, ingredientPage.getTotalPages());
    }

    public Integer createIngredient(Integer userId, Integer compartmentId, IngredientCreateRequest request) {
        User user = getUserById(userId);
        Compartment compartment = getCompartmentById(compartmentId);

        validateCompartmentAccess(compartment, user);

        IngredientType ingredientType = ingredientTypeRepository.findById(request.getIngredientTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("식재료 타입을 찾을 수 없습니다."));

        IngredientCategory ingredientCategory = null;
        if (request.getIngredientCategoryId() != null) {
            ingredientCategory = ingredientCategoryRepository.findById(request.getIngredientCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("식재료 카테고리를 찾을 수 없습니다."));
        }

        IngredientState freshState = ingredientStateRepository.findByValue(IngredientState.Type.FRESH.getValue())
                .orElseThrow(() -> new ResourceNotFoundException("FRESH 상태를 찾을 수 없습니다."));

        Ingredient ingredient = Ingredient.builder()
                .compartment(compartment)
                .ingredientType(ingredientType)
                .name(request.getName())
                .ingredientCategory(ingredientCategory)
                .memo(request.getMemo())
                .expirationDate(request.getExpirationDate())
                .ingredientState(freshState)
                .isDeleted(false)
                .build();

        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        log.info("Ingredient created: {} in compartment: {} by user: {}",
                savedIngredient.getId(), compartmentId, userId);

        return savedIngredient.getId();
    }

    @Transactional(readOnly = true)
    public PagedResponse<IngredientResponse> getFridgeIngredients(Integer userId, Integer fridgeId,
                                                                Integer page, Integer size, String sort, Integer state) {
        User user = getUserById(userId);
        Fridge fridge = getFridgeById(fridgeId);

        validateFridgeAccess(fridge, user);

        Pageable pageable = PageRequest.of(page, size);
        Page<Ingredient> ingredientPage;

        if (state != null) {
            IngredientState ingredientState = ingredientStateRepository.findById(state)
                    .orElseThrow(() -> new ResourceNotFoundException("식재료 상태를 찾을 수 없습니다."));
            ingredientPage = ingredientRepository.findByFridgeAndStateAndIsDeletedFalse(fridge, ingredientState, pageable);
        } else {
            ingredientPage = ingredientRepository.findByFridgeAndIsDeletedFalse(fridge, pageable);
        }

        List<IngredientResponse> ingredients = ingredientPage.getContent().stream()
                .map(this::mapToIngredientResponse)
                .collect(Collectors.toList());

        return PagedResponse.of(ingredients, (int) ingredientPage.getTotalElements(),
                page, ingredientPage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public IngredientDetailResponse getIngredientDetail(Integer userId, Integer ingredientId) {
        User user = getUserById(userId);
        Ingredient ingredient = getIngredientById(ingredientId);

        validateIngredientAccess(ingredient, user);

        return IngredientDetailResponse.builder()
                .ingredientId(ingredient.getId())
                .compartmentId(ingredient.getCompartment().getId())
                .ingredientTypeId(ingredient.getIngredientType().getId())
                .name(ingredient.getName())
                .ingredientCategoryId(ingredient.getIngredientCategory() != null ?
                        ingredient.getIngredientCategory().getId() : null)
                .memo(ingredient.getMemo())
                .expirationDate(ingredient.getExpirationDate())
                .ingredientStateId(ingredient.getIngredientState().getId())
                .createdAt(ingredient.getCreatedAt())
                .build();
    }

    public void updateIngredient(Integer userId, Integer ingredientId, IngredientUpdateRequest request) {
        User user = getUserById(userId);
        Ingredient ingredient = getIngredientById(ingredientId);

        validateIngredientAccess(ingredient, user);

        ingredient.updateInfo(request.getName(), request.getMemo(), request.getExpirationDate());
        log.info("Ingredient updated: {} by user: {}", ingredientId, userId);
    }

    public void deleteIngredient(Integer userId, Integer ingredientId) {
        User user = getUserById(userId);
        Ingredient ingredient = getIngredientById(ingredientId);

        validateIngredientAccess(ingredient, user);

        ingredient.softDelete();
        log.info("Ingredient deleted: {} by user: {}", ingredientId, userId);
    }

    public void changeIngredientState(Integer userId, Integer ingredientId, IngredientStateChangeRequest request) {
        User user = getUserById(userId);
        Ingredient ingredient = getIngredientById(ingredientId);

        validateIngredientAccess(ingredient, user);

        IngredientState newState = ingredientStateRepository.findById(request.getIngredientStateId())
                .orElseThrow(() -> new ResourceNotFoundException("식재료 상태를 찾을 수 없습니다."));

        ingredient.changeState(newState);
        log.info("Ingredient state changed: {} to state: {} by user: {}",
                ingredientId, request.getIngredientStateId(), userId);
    }

    private IngredientResponse mapToIngredientResponse(Ingredient ingredient) {
        return IngredientResponse.builder()
                .ingredientId(ingredient.getId())
                .name(ingredient.getName())
                .expirationDate(ingredient.getExpirationDate())
                .ingredientStateId(ingredient.getIngredientState().getId())
                .compartment(IngredientResponse.CompartmentInfo.builder()
                        .compartmentId(ingredient.getCompartment().getId())
                        .name(ingredient.getCompartment().getName())
                        .build())
                .build();
    }

    private User getUserById(Integer userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Compartment getCompartmentById(Integer compartmentId) {
        return compartmentRepository.findByIdAndIsDeletedFalse(compartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("보관칸을 찾을 수 없습니다."));
    }

    private Ingredient getIngredientById(Integer ingredientId) {
        return ingredientRepository.findByIdAndIsDeletedFalse(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("식재료를 찾을 수 없습니다."));
    }

    private Fridge getFridgeById(Integer fridgeId) {
        return fridgeRepository.findByIdAndIsDeletedFalse(fridgeId)
                .orElseThrow(() -> new ResourceNotFoundException("냉장고를 찾을 수 없습니다."));
    }

    private final FridgeRepository fridgeRepository;

    private void validateCompartmentAccess(Compartment compartment, User user) {
        Fridge fridge = compartment.getFridge();
        if (!fridge.isAccessibleBy(user)) {
            throw new BusinessException("보관칸에 접근할 권한이 없습니다.");
        }
    }

    private void validateIngredientAccess(Ingredient ingredient, User user) {
        Fridge fridge = ingredient.getCompartment().getFridge();
        if (!fridge.isAccessibleBy(user)) {
            throw new BusinessException("식재료에 접근할 권한이 없습니다.");
        }
    }

    private void validateFridgeAccess(Fridge fridge, User user) {
        if (!fridge.isAccessibleBy(user)) {
            throw new BusinessException("냉장고에 접근할 권한이 없습니다.");
        }
    }
}