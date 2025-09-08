package com.legacykeep.legacy.dto.response;

import com.legacykeep.legacy.entity.LegacyBucket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Response DTO for legacy bucket
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucketResponse {

    private UUID id;
    private String name;
    private String description;
    private UUID categoryId;
    private UUID creatorId;
    private UUID familyId;
    private LegacyBucket.BucketType bucketType;
    private LegacyBucket.PrivacyLevel privacyLevel;
    private Boolean isFeatured;
    private Integer sortOrder;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    // Additional computed fields
    private String fullPath;
    private Integer contentCount;
    private Long totalMediaSize;
    private String contentSummary;
    private Boolean canBeDeleted;
    private Boolean isEmpty;

    // Category information
    private CategoryResponse category;
}
