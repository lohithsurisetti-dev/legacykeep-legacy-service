package com.legacykeep.legacy.repository;

import com.legacykeep.legacy.entity.LegacyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for LegacyCategory entity
 * Handles database operations for hierarchical categories
 */
@Repository
public interface LegacyCategoryRepository extends JpaRepository<LegacyCategory, UUID> {

    /**
     * Find all root categories (no parent)
     */
    @Query("SELECT c FROM LegacyCategory c WHERE c.parentCategoryId IS NULL AND c.isActive = true ORDER BY c.sortOrder, c.name")
    List<LegacyCategory> findRootCategories();

    /**
     * Find all root categories (no parent) with pagination
     */
    @Query("SELECT c FROM LegacyCategory c WHERE c.parentCategoryId IS NULL AND c.isActive = true ORDER BY c.sortOrder, c.name")
    org.springframework.data.domain.Page<LegacyCategory> findRootCategories(org.springframework.data.domain.Pageable pageable);

    /**
     * Find all subcategories of a parent category
     */
    @Query("SELECT c FROM LegacyCategory c WHERE c.parentCategoryId = :parentId AND c.isActive = true ORDER BY c.sortOrder, c.name")
    List<LegacyCategory> findSubCategories(@Param("parentId") UUID parentId);

    /**
     * Find categories by type (SYSTEM or USER_CREATED)
     */
    @Query("SELECT c FROM LegacyCategory c WHERE c.categoryType = :type AND c.isActive = true ORDER BY c.sortOrder, c.name")
    List<LegacyCategory> findByCategoryType(@Param("type") LegacyCategory.CategoryType type);

    /**
     * Find categories by level in hierarchy
     */
    @Query("SELECT c FROM LegacyCategory c WHERE c.categoryLevel = :level AND c.isActive = true ORDER BY c.sortOrder, c.name")
    List<LegacyCategory> findByCategoryLevel(@Param("level") Integer level);

    /**
     * Find category by name (case insensitive)
     */
    @Query("SELECT c FROM LegacyCategory c WHERE LOWER(c.name) = LOWER(:name) AND c.isActive = true")
    Optional<LegacyCategory> findByNameIgnoreCase(@Param("name") String name);

    /**
     * Find categories by name containing (case insensitive)
     */
    @Query("SELECT c FROM LegacyCategory c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.isActive = true ORDER BY c.sortOrder, c.name")
    List<LegacyCategory> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Check if category name exists (excluding given category)
     */
    @Query("SELECT COUNT(c) > 0 FROM LegacyCategory c WHERE LOWER(c.name) = LOWER(:name) AND c.id != :excludeId AND c.isActive = true")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("excludeId") UUID excludeId);

    /**
     * Find all active categories
     */
    @Query("SELECT c FROM LegacyCategory c WHERE c.isActive = true ORDER BY c.sortOrder, c.name")
    List<LegacyCategory> findAllActive();

    /**
     * Find categories that can be deleted (no subcategories and no buckets)
     */
    @Query("SELECT c FROM LegacyCategory c WHERE c.isActive = true AND c.id NOT IN " +
           "(SELECT DISTINCT sc.parentCategoryId FROM LegacyCategory sc WHERE sc.parentCategoryId IS NOT NULL) " +
           "AND c.id NOT IN (SELECT DISTINCT b.categoryId FROM LegacyBucket b)")
    List<LegacyCategory> findDeletableCategories();

    /**
     * Count subcategories for a parent category
     */
    @Query("SELECT COUNT(c) FROM LegacyCategory c WHERE c.parentCategoryId = :parentId AND c.isActive = true")
    long countSubCategories(@Param("parentId") UUID parentId);

    /**
     * Find category path (all ancestors) - simplified version
     */
    @Query("SELECT c FROM LegacyCategory c WHERE c.id = :categoryId")
    Optional<LegacyCategory> findCategoryById(@Param("categoryId") UUID categoryId);
}
