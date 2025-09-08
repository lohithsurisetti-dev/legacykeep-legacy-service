package com.legacykeep.legacy.dto.response;

import com.legacykeep.legacy.entity.LegacyContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for legacy content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponse {

    private UUID id;
    private String title;
    private String content;
    private LegacyContent.ContentType contentType;
    private UUID bucketId;
    private UUID creatorId;
    private UUID familyId;
    private Integer generationLevel;
    private LegacyContent.PrivacyLevel privacyLevel;
    private LegacyContent.ContentStatus status;
    private Boolean isFeatured;
    private Integer sortOrder;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    // Additional computed fields
    private String fullPath;
    private String contentPreview;
    private String contentSummary;
    private Boolean canBeDeleted;
    private Boolean isActive;

    // Related entities
    private BucketResponse bucket;
    private List<MediaFileResponse> mediaFiles;
    private List<RecipientResponse> recipients;

    // Interaction data (from Interaction Service)
    private InteractionSummaryResponse interactions;
}
