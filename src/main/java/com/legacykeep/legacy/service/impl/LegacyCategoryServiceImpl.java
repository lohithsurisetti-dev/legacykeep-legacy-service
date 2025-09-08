package com.legacykeep.legacy.service.impl;

import com.legacykeep.legacy.dto.request.CreateCategoryRequest;
import com.legacykeep.legacy.dto.response.CategoryResponse;
import com.legacykeep.legacy.entity.LegacyCategory;
import com.legacykeep.legacy.repository.LegacyCategoryRepository;
import com.legacykeep.legacy.service.LegacyCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of LegacyCategoryService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LegacyCategoryServiceImpl implements LegacyCategoryService {

    private final LegacyCategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("Creating new category: {}", request.getName());
        
        // Validate parent category exists if provided
        if (request.getParentCategoryId() != null) {
            categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found with ID: " + request.getParentCategoryId()));
        }

        LegacyCategory category = LegacyCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .parentCategoryId(request.getParentCategoryId())
                .icon(request.getIcon())
                .color(request.getColor())
                .sortOrder(request.getSortOrder())
                .build();

        LegacyCategory savedCategory = categoryRepository.save(category);
        log.info("Created category with ID: {}", savedCategory.getId());

        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getRootCategories() {
        log.info("Fetching root categories");
        return categoryRepository.findRootCategories()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CategoryResponse> searchCategories(String keyword, Pageable pageable, LegacyCategory.CategoryType categoryType) {
        log.info("Searching categories with keyword: '{}', categoryType: {}", keyword, categoryType);
        
        // For now, return all categories with basic pagination
        // TODO: Implement proper search logic
        Page<LegacyCategory> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.map(this::mapToResponse);
    }

    @Override
    public CategoryResponse updateCategory(UUID id, CreateCategoryRequest request) {
        log.info("Updating category: {}", id);
        
        LegacyCategory existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));
        
        // Update fields
        existingCategory.setName(request.getName());
        existingCategory.setDescription(request.getDescription());
        existingCategory.setParentCategoryId(request.getParentCategoryId());
        existingCategory.setIcon(request.getIcon());
        existingCategory.setColor(request.getColor());
        existingCategory.setSortOrder(request.getSortOrder());
        
        LegacyCategory updatedCategory = categoryRepository.save(existingCategory);
        return mapToResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID id) {
        log.info("Deleting category: {}", id);
        
        LegacyCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));
        
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getCategoriesWithFilters(
            Pageable pageable,
            UUID parentId,
            LegacyCategory.CategoryType categoryType,
            Boolean isActive,
            String sortBy,
            String sortDir) {
        
        log.info("Fetching categories with filters - parentId: {}, categoryType: {}, isActive: {}", 
                parentId, categoryType, isActive);
        
        // For now, return all categories with basic pagination
        // TODO: Implement proper filtering logic
        Page<LegacyCategory> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubCategories(UUID parentId) {
        log.info("Fetching subcategories for parent: {}", parentId);
        return categoryRepository.findSubCategories(parentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {
        log.info("Fetching category by ID: {}", id);
        LegacyCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));
        return mapToResponse(category);
    }

    private CategoryResponse mapToResponse(LegacyCategory category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentCategoryId(category.getParentCategoryId())
                .categoryType(category.getCategoryType())
                .icon(category.getIcon())
                .color(category.getColor())
                .sortOrder(category.getSortOrder())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}