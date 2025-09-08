package com.legacykeep.legacy.dto.response;

import com.legacykeep.legacy.entity.LegacyCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for legacy category
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private UUID id;
    private String name;
    private String description;
    private UUID parentCategoryId;
    private Integer categoryLevel;
    private LegacyCategory.CategoryType categoryType;
    private String icon;
    private String color;
    private Integer sortOrder;
    private Boolean isActive;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    // Additional computed fields
    private String fullPath;
    private Boolean hasSubCategories;
    private Boolean canBeDeleted;
    private Integer subCategoryCount;
    private Integer bucketCount;

    // Nested subcategories (for hierarchical display)
    private List<CategoryResponse> subCategories;
}
