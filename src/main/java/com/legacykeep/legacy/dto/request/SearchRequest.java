package com.legacykeep.legacy.dto.request;

import com.legacykeep.legacy.entity.LegacyContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for advanced search functionality.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    
    /**
     * Search query string
     */
    @Size(max = 500, message = "Search query cannot exceed 500 characters")
    private String query;
    
    /**
     * Filter by bucket IDs
     */
    private List<UUID> bucketIds;
    
    /**
     * Filter by category IDs
     */
    private List<UUID> categoryIds;
    
    /**
     * Filter by content types
     */
    private List<LegacyContent.ContentType> contentTypes;
    
    /**
     * Filter by creator IDs
     */
    private List<UUID> creatorIds;
    
    /**
     * Filter by family ID
     */
    private UUID familyId;
    
    /**
     * Filter by tags
     */
    private List<String> tags;
    
    /**
     * Filter by featured status
     */
    private Boolean featured;
    
    /**
     * Filter by date range - start date
     */
    private LocalDateTime dateFrom;
    
    /**
     * Filter by date range - end date
     */
    private LocalDateTime dateTo;
    
    /**
     * Filter by generation level
     */
    private Integer generationLevel;
    
    /**
     * Filter by privacy level
     */
    private String privacyLevel;
    
    /**
     * Sort field
     */
    private String sortBy;
    
    /**
     * Sort direction (asc, desc)
     */
    private String sortDirection;
    
    /**
     * Page number (0-based)
     */
    @Min(value = 0, message = "Page number must be non-negative")
    private Integer page;
    
    /**
     * Page size
     */
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private Integer size;
    
    /**
     * Include content body in search
     */
    private Boolean includeContent;
    
    /**
     * Search in media file names
     */
    private Boolean searchMediaFiles;
    
    /**
     * Search in recipient information
     */
    private Boolean searchRecipients;
}
