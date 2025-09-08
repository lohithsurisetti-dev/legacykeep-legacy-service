package com.legacykeep.legacy.service.impl;

import com.legacykeep.legacy.dto.request.CreateCategoryRequest;
import com.legacykeep.legacy.dto.response.CategoryResponse;
import com.legacykeep.legacy.entity.LegacyCategory;
import com.legacykeep.legacy.repository.LegacyCategoryRepository;
import com.legacykeep.legacy.service.LegacyCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing legacy categories
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

        // Validate parent category exists if specified
        if (request.getParentCategoryId() != null) {
            LegacyCategory parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            
            if (!parentCategory.getIsActive()) {
                throw new IllegalArgumentException("Parent category is not active");
            }
        }

        // Check if category name already exists
        if (categoryRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
        }

        LegacyCategory category = LegacyCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .parentCategoryId(request.getParentCategoryId())
                .categoryLevel(calculateCategoryLevel(request.getParentCategoryId()))
                .categoryType(LegacyCategory.CategoryType.USER_CREATED)
                .icon(request.getIcon())
                .color(request.getColor())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isActive(true)
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
        existingCategory.setCategoryType(request.getCategoryType());
        existingCategory.setIcon(request.getIcon());
        existingCategory.setColor(request.getColor());
        existingCategory.setSortOrder(request.getSortOrder());
        existingCategory.setIsActive(request.getIsActive());
        
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
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        
        return mapToResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.info("Fetching all categories");
        return categoryRepository.findAllActive()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByType(LegacyCategory.CategoryType type) {
        log.info("Fetching categories by type: {}", type);
        return categoryRepository.findByCategoryType(type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse updateCategory(UUID id, CreateCategoryRequest request) {
        log.info("Updating category: {}", id);

        LegacyCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Check if name is being changed and if it already exists
        if (!category.getName().equals(request.getName())) {
            if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
                throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
            }
        }

        // Validate parent category if being changed
        if (request.getParentCategoryId() != null && !request.getParentCategoryId().equals(category.getParentCategoryId())) {
            LegacyCategory parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            
            if (!parentCategory.getIsActive()) {
                throw new IllegalArgumentException("Parent category is not active");
            }
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setParentCategoryId(request.getParentCategoryId());
        category.setCategoryLevel(calculateCategoryLevel(request.getParentCategoryId()));
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }

        LegacyCategory updatedCategory = categoryRepository.save(category);
        log.info("Updated category: {}", updatedCategory.getId());

        return mapToResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID id) {
        log.info("Deleting category: {}", id);

        LegacyCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (!category.canBeDeleted()) {
            throw new IllegalStateException("Category cannot be deleted as it has subcategories or buckets");
        }

        category.setIsActive(false);
        categoryRepository.save(category);
        log.info("Deleted category: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategories(String name) {
        log.info("Searching categories by name: {}", name);
        return categoryRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Calculate category level based on parent
     */
    private Integer calculateCategoryLevel(UUID parentId) {
        if (parentId == null) {
            return 1; // Root category
        }
        
        LegacyCategory parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
        
        return parent.getCategoryLevel() + 1;
    }

    /**
     * Map entity to response DTO
     */
    private CategoryResponse mapToResponse(LegacyCategory category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentCategoryId(category.getParentCategoryId())
                .categoryLevel(category.getCategoryLevel())
                .categoryType(category.getCategoryType())
                .icon(category.getIcon())
                .color(category.getColor())
                .sortOrder(category.getSortOrder())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .fullPath(category.getFullPath())
                .hasSubCategories(category.hasSubCategories())
                .canBeDeleted(category.canBeDeleted())
                .subCategoryCount((int) categoryRepository.countSubCategories(category.getId()))
                .build();
    }
}
