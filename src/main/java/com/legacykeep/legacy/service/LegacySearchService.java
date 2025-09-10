package com.legacykeep.legacy.service;

import com.legacykeep.legacy.dto.request.SearchRequest;
import com.legacykeep.legacy.dto.response.ContentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for legacy content search and discovery.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public interface LegacySearchService {
    
    /**
     * Perform advanced search with multiple filters and facets.
     * 
     * @param request Search request with filters and pagination
     * @return Search results with pagination
     */
    Page<ContentResponse> searchContent(SearchRequest request);
    
    /**
     * Search content by keyword with basic filters.
     * 
     * @param query Search keyword
     * @param pageable Pagination information
     * @param bucketId Optional bucket filter
     * @param creatorId Optional creator filter
     * @return Search results
     */
    Page<ContentResponse> searchContent(String query, Pageable pageable, UUID bucketId, UUID creatorId);
    
    /**
     * Get content recommendations for a user.
     * 
     * @param userId User ID
     * @param familyId Family ID
     * @param type Recommendation type
     * @param limit Number of recommendations
     * @return Content recommendations
     */
    List<ContentResponse> getRecommendations(UUID userId, UUID familyId, String type, Integer limit);
    
    /**
     * Get legacy timeline for a family.
     * 
     * @param familyId Family ID
     * @param generationLevel Generation level filter
     * @param year Year filter
     * @param month Month filter
     * @param pageable Pagination information
     * @return Timeline entries
     */
    Page<ContentResponse> getTimeline(UUID familyId, Integer generationLevel, 
                               Integer year, Integer month, Pageable pageable);
    
    /**
     * Get trending content in a family.
     * 
     * @param familyId Family ID
     * @param period Time period (7d, 30d, 90d, 1y)
     * @param limit Number of results
     * @return Trending content
     */
    List<ContentResponse> getTrendingContent(UUID familyId, String period, Integer limit);
    
    /**
     * Get similar content based on a content item.
     * 
     * @param contentId Content ID
     * @param limit Number of similar items
     * @return Similar content
     */
    List<ContentResponse> getSimilarContent(UUID contentId, Integer limit);
    
    /**
     * Get search suggestions based on partial query.
     * 
     * @param query Partial search query
     * @param limit Number of suggestions
     * @return Search suggestions
     */
    List<String> getSearchSuggestions(String query, Integer limit);
    
    /**
     * Get search analytics for a family.
     * 
     * @param familyId Family ID
     * @param period Time period
     * @return Search analytics
     */
    Map<String, Object> getSearchAnalytics(UUID familyId, String period);
}
