package com.legacykeep.legacy.controller;

import com.legacykeep.legacy.dto.request.SearchRequest;
import com.legacykeep.legacy.dto.response.ApiResponse;
import com.legacykeep.legacy.dto.response.ContentResponse;
import com.legacykeep.legacy.service.LegacySearchService;
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
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for legacy content search and discovery.
 * 
 * Provides advanced search capabilities, recommendations, and timeline features.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class LegacySearchController {
    
    private final LegacySearchService searchService;
    
    /**
     * Perform advanced search with multiple filters and facets.
     */
    @PostMapping("/advanced")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> advancedSearch(@Valid @RequestBody SearchRequest request) {
        log.info("Performing advanced search with query: '{}'", request.getQuery());
        Page<ContentResponse> response = searchService.searchContent(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Advanced search completed successfully"));
    }
    
    /**
     * Search content by keyword with basic filters.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> searchContent(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID bucketId,
            @RequestParam(required = false) UUID creatorId) {
        
        log.info("Searching content with keyword: '{}', page: {}, size: {}", q, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentResponse> response = searchService.searchContent(q, pageable, bucketId, creatorId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Search completed successfully"));
    }
    
    /**
     * Get content recommendations for a user.
     */
    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<ContentResponse>>> getRecommendations(
            @RequestParam UUID userId,
            @RequestParam UUID familyId,
            @RequestParam(defaultValue = "SIMILAR_CONTENT") String type,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        log.info("Getting recommendations for user: {} in family: {} of type: {}", 
                userId, familyId, type);
        
        List<ContentResponse> response = searchService.getRecommendations(userId, familyId, type, limit);
        return ResponseEntity.ok(ApiResponse.success(response, "Recommendations retrieved successfully"));
    }
    
    /**
     * Get legacy timeline for a family.
     */
    @GetMapping("/timeline")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> getTimeline(
            @RequestParam UUID familyId,
            @RequestParam(required = false) Integer generationLevel,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting timeline for family: {} generation: {} year: {} month: {}", 
                familyId, generationLevel, year, month);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentResponse> response = searchService.getTimeline(familyId, generationLevel, year, month, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Timeline retrieved successfully"));
    }
    
    /**
     * Get trending content in a family.
     */
    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<ContentResponse>>> getTrendingContent(
            @RequestParam UUID familyId,
            @RequestParam(defaultValue = "30d") String period,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        log.info("Getting trending content for family: {} period: {}", familyId, period);
        
        List<ContentResponse> response = searchService.getTrendingContent(familyId, period, limit);
        return ResponseEntity.ok(ApiResponse.success(response, "Trending content retrieved successfully"));
    }
    
    /**
     * Get similar content based on a content item.
     */
    @GetMapping("/similar/{contentId}")
    public ResponseEntity<ApiResponse<List<ContentResponse>>> getSimilarContent(
            @PathVariable UUID contentId,
            @RequestParam(defaultValue = "5") Integer limit) {
        
        log.info("Getting similar content for: {} limit: {}", contentId, limit);
        
        List<ContentResponse> response = searchService.getSimilarContent(contentId, limit);
        return ResponseEntity.ok(ApiResponse.success(response, "Similar content retrieved successfully"));
    }
    
    /**
     * Get search suggestions based on partial query.
     */
    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getSearchSuggestions(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "5") Integer limit) {
        
        log.debug("Getting search suggestions for query: '{}'", q);
        
        List<String> suggestions = searchService.getSearchSuggestions(q, limit);
        return ResponseEntity.ok(ApiResponse.success(suggestions, "Search suggestions retrieved successfully"));
    }
    
    /**
     * Get search analytics for a family.
     */
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchAnalytics(
            @RequestParam UUID familyId,
            @RequestParam(defaultValue = "30d") String period) {
        
        log.info("Getting search analytics for family: {} period: {}", familyId, period);
        
        Map<String, Object> analytics = searchService.getSearchAnalytics(familyId, period);
        return ResponseEntity.ok(ApiResponse.success(analytics, "Search analytics retrieved successfully"));
    }
    
    /**
     * Get popular search terms.
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<String>>> getPopularSearchTerms(
            @RequestParam(defaultValue = "10") Integer limit) {
        
        log.debug("Getting popular search terms, limit: {}", limit);
        
        List<String> popularTerms = searchService.getSearchSuggestions(null, limit);
        return ResponseEntity.ok(ApiResponse.success(popularTerms, "Popular search terms retrieved successfully"));
    }
    
    /**
     * Get content by category with search.
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> searchByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Searching content by category: {} with query: '{}'", categoryId, q);
        
        SearchRequest request = SearchRequest.builder()
                .query(q)
                .categoryIds(List.of(categoryId))
                .page(page)
                .size(size)
                .build();
        
        Page<ContentResponse> response = searchService.searchContent(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Category search completed successfully"));
    }
    
    /**
     * Get content by tags with search.
     */
    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> searchByTags(
            @RequestParam List<String> tags,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Searching content by tags: {} with query: '{}'", tags, q);
        
        SearchRequest request = SearchRequest.builder()
                .query(q)
                .tags(tags)
                .page(page)
                .size(size)
                .build();
        
        Page<ContentResponse> response = searchService.searchContent(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Tag search completed successfully"));
    }
    
    /**
     * Get content by date range with search.
     */
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> searchByDateRange(
            @RequestParam String dateFrom,
            @RequestParam String dateTo,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Searching content by date range: {} to {} with query: '{}'", dateFrom, dateTo, q);
        
        SearchRequest request = SearchRequest.builder()
                .query(q)
                .dateFrom(java.time.LocalDateTime.parse(dateFrom))
                .dateTo(java.time.LocalDateTime.parse(dateTo))
                .page(page)
                .size(size)
                .build();
        
        Page<ContentResponse> response = searchService.searchContent(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Date range search completed successfully"));
    }
}
