package com.legacykeep.legacy.dto.request;

import com.legacykeep.legacy.entity.LegacyBucket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a new legacy bucket
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBucketRequest {

    @NotBlank(message = "Bucket name is required")
    @Size(max = 255, message = "Bucket name must not exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Category is required")
    private UUID categoryId;

    @NotNull(message = "Creator is required")
    private UUID creatorId;

    @NotNull(message = "Family is required")
    private UUID familyId;

    @Builder.Default
    private LegacyBucket.BucketType bucketType = LegacyBucket.BucketType.CUSTOM;

    @Builder.Default
    private LegacyBucket.PrivacyLevel privacyLevel = LegacyBucket.PrivacyLevel.FAMILY;

    private Boolean isFeatured;

    private Integer sortOrder;
}
