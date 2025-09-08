package com.legacykeep.legacy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * LegacyCategory Entity
 * 
 * Represents hierarchical categories for organizing legacy content.
 * Supports both system-defined and user-created categories.
 * 
 * Features:
 * - Hierarchical structure with parent-child relationships
 * - System and user-created category types
 * - UI customization with icons and colors
 * - Sort ordering for consistent display
 * - Soft delete capability
 */
@Entity
@Table(name = "legacy_categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegacyCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @NotBlank(message = "Category name is required")
    @Size(max = 255, message = "Category name must not exceed 255 characters")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "parent_category_id", columnDefinition = "UUID")
    private UUID parentCategoryId;

    @NotNull(message = "Category level is required")
    @Column(name = "category_level", nullable = false)
    @Builder.Default
    private Integer categoryLevel = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", nullable = false, length = 50)
    @Builder.Default
    private CategoryType categoryType = CategoryType.SYSTEM;

    @Size(max = 100, message = "Icon must not exceed 100 characters")
    @Column(name = "icon", length = 100)
    private String icon;

    @Size(max = 20, message = "Color must not exceed 20 characters")
    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    // ==============================================
    // RELATIONSHIPS
    // ==============================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id", insertable = false, updatable = false)
    private LegacyCategory parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LegacyCategory> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LegacyBucket> buckets = new ArrayList<>();

    // ==============================================
    // ENUMS
    // ==============================================

    public enum CategoryType {
        SYSTEM("System-defined categories"),
        USER_CREATED("User-created categories");

        private final String description;

        CategoryType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==============================================
    // BUSINESS LOGIC METHODS
    // ==============================================

    /**
     * Check if this category is a root category (no parent)
     */
    public boolean isRootCategory() {
        return parentCategoryId == null;
    }

    /**
     * Check if this category has subcategories
     */
    public boolean hasSubCategories() {
        return subCategories != null && !subCategories.isEmpty();
    }

    /**
     * Get the full category path (e.g., "Personal & Family > Letters & Messages")
     */
    public String getFullPath() {
        if (isRootCategory()) {
            return name;
        }
        
        StringBuilder path = new StringBuilder();
        LegacyCategory current = this;
        
        while (current != null) {
            if (path.length() > 0) {
                path.insert(0, " > ");
            }
            path.insert(0, current.getName());
            current = current.getParentCategory();
        }
        
        return path.toString();
    }

    /**
     * Check if this category can be deleted
     * (Cannot delete if it has subcategories or buckets)
     */
    public boolean canBeDeleted() {
        return !hasSubCategories() && (buckets == null || buckets.isEmpty());
    }

    /**
     * Get the depth level of this category in the hierarchy
     */
    public int getDepth() {
        int depth = 1;
        LegacyCategory current = parentCategory;
        while (current != null) {
            depth++;
            current = current.getParentCategory();
        }
        return depth;
    }

    // ==============================================
    // VALIDATION METHODS
    // ==============================================

    /**
     * Validate that the category level matches its actual depth
     */
    public boolean isCategoryLevelValid() {
        return categoryLevel != null && categoryLevel.equals(getDepth());
    }

    /**
     * Validate that the parent category is not itself
     */
    public boolean isParentCategoryValid() {
        if (parentCategoryId == null) {
            return true; // Root category is valid
        }
        return !parentCategoryId.equals(id);
    }

    // ==============================================
    // UTILITY METHODS
    // ==============================================

    @PrePersist
    @PreUpdate
    private void validateCategory() {
        if (!isCategoryLevelValid()) {
            throw new IllegalStateException("Category level does not match hierarchy depth");
        }
        if (!isParentCategoryValid()) {
            throw new IllegalStateException("Category cannot be its own parent");
        }
    }

    @Override
    public String toString() {
        return String.format("LegacyCategory{id=%s, name='%s', level=%d, type=%s, active=%s}", 
                id, name, categoryLevel, categoryType, isActive);
    }
}
