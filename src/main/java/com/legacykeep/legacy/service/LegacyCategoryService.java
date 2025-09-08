package com.legacykeep.legacy.service;

import com.legacykeep.legacy.dto.request.CreateCategoryRequest;
import com.legacykeep.legacy.dto.response.CategoryResponse;
import com.legacykeep.legacy.entity.LegacyCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing legacy categories.
 * 
 * Provides business logic for hierarchical category management with clean, meaningful operations.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public interface LegacyCategoryService {

    /**
     * Create a new category
     */
    CategoryResponse createCategory(CreateCategoryRequest request);

    /**
     * Get category by ID
     */
    CategoryResponse getCategoryById(UUID id);

    /**
     * Get categories with pagination and filters
     */
    Page<CategoryResponse> getCategoriesWithFilters(
            Pageable pageable,
            UUID parentId,
            LegacyCategory.CategoryType categoryType,
            Boolean isActive,
            String sortBy,
            String sortDir);

    /**
     * Get root categories (categories with no parent)
     */
    List<CategoryResponse> getRootCategories();

    /**
     * Get subcategories of a parent category
     */
    List<CategoryResponse> getSubCategories(UUID parentId);

    /**
     * Search categories by keyword with pagination and filters
     */
    Page<CategoryResponse> searchCategories(
            String keyword,
            Pageable pageable,
            LegacyCategory.CategoryType categoryType);

    /**
     * Update category
     */
    CategoryResponse updateCategory(UUID id, CreateCategoryRequest request);

    /**
     * Delete category
     */
    void deleteCategory(UUID id);
}