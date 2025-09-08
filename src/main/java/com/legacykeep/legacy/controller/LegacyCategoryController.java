package com.legacykeep.legacy.controller;

import com.legacykeep.legacy.dto.request.CreateCategoryRequest;
import com.legacykeep.legacy.dto.response.ApiResponse;
import com.legacykeep.legacy.dto.response.CategoryResponse;
import com.legacykeep.legacy.entity.LegacyCategory;
import com.legacykeep.legacy.service.LegacyCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing legacy categories.
 * 
 * Provides clean, meaningful endpoints for hierarchical category management with pagination and filtering.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class LegacyCategoryController {

    private final LegacyCategoryService categoryService;

    /**
     * Create a new category
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        log.info("Creating new category: {}", request.getName());
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Category created successfully"));
    }

    /**
     * Get category by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable UUID id) {
        log.debug("Getting category by ID: {}", id);
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Category retrieved successfully"));
    }

    /**
     * Get categories with pagination and optional filters
     * 
     * Query parameters:
     * - page: Page number (default: 0)
     * - size: Page size (default: 10)
     * - parentId: Filter by parent category ID (null for root categories)
     * - categoryType: Filter by category type (SYSTEM, USER_CREATED)
     * - isActive: Filter by active status (true/false)
     * - sortBy: Sort field (createdAt, updatedAt, name, sortOrder)
     * - sortDir: Sort direction (asc, desc)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID parentId,
            @RequestParam(required = false) LegacyCategory.CategoryType categoryType,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "sortOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Getting categories with filters - page: {}, size: {}, parentId: {}, categoryType: {}, isActive: {}", 
                page, size, parentId, categoryType, isActive);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryResponse> response = categoryService.getCategoriesWithFilters(
                pageable, parentId, categoryType, isActive, sortBy, sortDir);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Categories retrieved successfully"));
    }

    /**
     * Get root categories (categories with no parent)
     * 
     * This is a convenience endpoint for getting top-level categories.
     */
    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories() {
        log.info("Getting root categories");
        List<CategoryResponse> response = categoryService.getRootCategories();
        return ResponseEntity.ok(ApiResponse.success(response, "Root categories retrieved successfully"));
    }

    /**
     * Get subcategories of a parent category
     * 
     * This endpoint returns all direct children of the specified parent category.
     */
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getSubCategories(@PathVariable UUID parentId) {
        log.info("Getting subcategories for parent: {}", parentId);
        List<CategoryResponse> response = categoryService.getSubCategories(parentId);
        return ResponseEntity.ok(ApiResponse.success(response, "Subcategories retrieved successfully"));
    }

    /**
     * Search categories by keyword with pagination
     * 
     * Query parameters:
     * - q: Search keyword
     * - page: Page number (default: 0)
     * - size: Page size (default: 10)
     * - categoryType: Filter by category type (optional)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> searchCategories(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LegacyCategory.CategoryType categoryType) {
        
        log.info("Searching categories with keyword: '{}', page: {}, size: {}, categoryType: {}", 
                q, page, size, categoryType);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryResponse> response = categoryService.searchCategories(q, pageable, categoryType);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Search results retrieved successfully"));
    }

    /**
     * Update category
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCategoryRequest request) {
        log.info("Updating category: {}", id);
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Category updated successfully"));
    }

    /**
     * Delete category
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        log.info("Deleting category: {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }
}