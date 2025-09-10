package com.legacykeep.legacy.service.impl;

import com.legacykeep.legacy.dto.request.SearchRequest;
import com.legacykeep.legacy.dto.response.ContentResponse;
import com.legacykeep.legacy.entity.LegacyContent;
import com.legacykeep.legacy.repository.LegacyContentRepository;
import com.legacykeep.legacy.service.LegacyContentService;
import com.legacykeep.legacy.service.LegacySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of LegacySearchService for advanced search and discovery.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LegacySearchServiceImpl implements LegacySearchService {
    
    private final LegacyContentRepository contentRepository;
    private final LegacyContentService contentService;
    
    @Override
    public Page<ContentResponse> searchContent(SearchRequest request) {
        log.info("Performing advanced search with query: '{}'", request.getQuery());
        
        // Build pageable
        Pageable pageable = buildPageable(request);
        
        // Perform search based on query type
        Page<LegacyContent> contentPage;
        if (request.getQuery() != null && !request.getQuery().trim().isEmpty()) {
            contentPage = performTextSearch(request, pageable);
        } else {
            contentPage = performFilteredSearch(request, pageable);
        }
        
        // Convert to response DTOs
        return contentPage.map(this::convertToContentResponse);
    }
    
    @Override
    public Page<ContentResponse> searchContent(String query, Pageable pageable, UUID bucketId, UUID creatorId) {
        log.info("Performing basic search with query: '{}'", query);
        
        Page<LegacyContent> contentPage;
        if (query != null && !query.trim().isEmpty()) {
            // Text search - use existing repository method
            List<LegacyContent> results = contentRepository.findByTitleContainingIgnoreCase(query);
            // Convert to page manually since repository doesn't have paginated version
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), results.size());
            List<LegacyContent> pageContent = results.subList(start, end);
            contentPage = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, results.size());
        } else {
            // Filtered search
            contentPage = contentRepository.findAllActive(pageable);
        }
        
        // Apply additional filters
        if (bucketId != null) {
            contentPage = contentRepository.findByBucketId(bucketId, pageable);
        }
        if (creatorId != null) {
            contentPage = contentRepository.findByCreatorId(creatorId, pageable);
        }
        
        return contentPage.map(this::convertToContentResponse);
    }
    
    @Override
    public List<ContentResponse> getRecommendations(UUID userId, UUID familyId, String type, Integer limit) {
        log.info("Getting recommendations for user: {} in family: {} of type: {}", 
                userId, familyId, type);
        
        List<ContentResponse> recommendations = new ArrayList<>();
        
        switch (type) {
            case "SIMILAR_CONTENT":
                recommendations = getSimilarContentRecommendations(userId, familyId, limit);
                break;
            case "FAMILY_TRENDING":
                recommendations = getTrendingContentRecommendations(familyId, limit);
                break;
            case "GENERATION_BASED":
                recommendations = getGenerationBasedRecommendations(userId, familyId, limit);
                break;
            case "CATEGORY_BASED":
                recommendations = getCategoryBasedRecommendations(userId, familyId, limit);
                break;
            case "RECENT_ACTIVITY":
                recommendations = getRecentActivityRecommendations(familyId, limit);
                break;
            default:
                recommendations = getDefaultRecommendations(familyId, limit);
        }
        
        return recommendations;
    }
    
    @Override
    public Page<ContentResponse> getTimeline(UUID familyId, Integer generationLevel, 
                                      Integer year, Integer month, Pageable pageable) {
        log.info("Getting timeline for family: {} generation: {} year: {} month: {}", 
                familyId, generationLevel, year, month);
        
        // Build date range
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        
        if (year != null) {
            startDate = LocalDateTime.of(year, 1, 1, 0, 0);
            endDate = LocalDateTime.of(year, 12, 31, 23, 59);
            
            if (month != null) {
                startDate = LocalDateTime.of(year, month, 1, 0, 0);
                endDate = startDate.plusMonths(1).minusDays(1);
            }
        }
        
        // Get content for timeline
        List<LegacyContent> allContent = contentRepository.findByFamilyId(familyId);
        
        // Filter by date range if specified
        if (startDate != null && endDate != null) {
            // Convert LocalDateTime to ZonedDateTime for repository method
            java.time.ZonedDateTime zonedStartDate = startDate.atZone(java.time.ZoneId.systemDefault());
            java.time.ZonedDateTime zonedEndDate = endDate.atZone(java.time.ZoneId.systemDefault());
            allContent = contentRepository.findByCreatedAtBetween(zonedStartDate, zonedEndDate);
        }
        
        // Convert to page manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allContent.size());
        List<LegacyContent> pageContent = allContent.subList(start, end);
        Page<LegacyContent> contentPage = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, allContent.size());
        
        return contentPage.map(this::convertToContentResponse);
    }
    
    @Override
    public List<ContentResponse> getTrendingContent(UUID familyId, String period, Integer limit) {
        log.info("Getting trending content for family: {} period: {}", familyId, period);
        
        // Calculate date range based on period
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = switch (period) {
            case "7d" -> endDate.minusDays(7);
            case "30d" -> endDate.minusDays(30);
            case "90d" -> endDate.minusDays(90);
            case "1y" -> endDate.minusYears(1);
            default -> endDate.minusDays(30);
        };
        
        // Get trending content (most recent content in period)
        // Convert LocalDateTime to ZonedDateTime for repository method
        java.time.ZonedDateTime zonedStartDate = startDate.atZone(java.time.ZoneId.systemDefault());
        java.time.ZonedDateTime zonedEndDate = endDate.atZone(java.time.ZoneId.systemDefault());
        List<LegacyContent> allContent = contentRepository.findByCreatedAtBetween(zonedStartDate, zonedEndDate);
        
        // Sort by creation date descending and limit
        allContent = allContent.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
        
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<LegacyContent> contentPage = new org.springframework.data.domain.PageImpl<>(allContent, pageable, allContent.size());
        
        return contentPage.getContent()
                .stream()
                .map(this::convertToContentResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ContentResponse> getSimilarContent(UUID contentId, Integer limit) {
        log.info("Getting similar content for: {} limit: {}", contentId, limit);
        
        // Get the source content
        LegacyContent sourceContent = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found: " + contentId));
        
        // Find similar content based on bucket (since categoryId doesn't exist in entity)
        Pageable pageable = PageRequest.of(0, limit);
        Page<LegacyContent> contentPage = contentRepository.findByBucketId(sourceContent.getBucketId(), pageable);
        
        return contentPage.getContent()
                .stream()
                .filter(content -> !content.getId().equals(contentId)) // Exclude source content
                .map(this::convertToContentResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getSearchSuggestions(String query, Integer limit) {
        log.debug("Getting search suggestions for query: '{}'", query);
        
        if (query == null || query.trim().isEmpty()) {
            return getPopularSearchTerms(limit);
        }
        
        // Get suggestions based on content titles and descriptions
        List<String> suggestions = new ArrayList<>();
        
        // Add title-based suggestions
        List<LegacyContent> titleMatches = contentRepository
                .findByTitleContainingIgnoreCase(query);
        suggestions.addAll(titleMatches.stream()
                .map(LegacyContent::getTitle)
                .limit(limit / 2)
                .collect(Collectors.toList()));
        
        // Add category-based suggestions
        List<String> categorySuggestions = getCategorySuggestions(query, limit / 2);
        suggestions.addAll(categorySuggestions);
        
        return suggestions.stream()
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getSearchAnalytics(UUID familyId, String period) {
        log.info("Getting search analytics for family: {} period: {}", familyId, period);
        
        Map<String, Object> analytics = new HashMap<>();
        
        // Calculate date range
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = switch (period) {
            case "7d" -> endDate.minusDays(7);
            case "30d" -> endDate.minusDays(30);
            case "90d" -> endDate.minusDays(90);
            case "1y" -> endDate.minusYears(1);
            default -> endDate.minusDays(30);
        };
        
        // Get content statistics
        long totalContent = contentRepository.findByFamilyId(familyId).size();
        // Convert LocalDateTime to ZonedDateTime for repository method
        java.time.ZonedDateTime zonedStartDate = startDate.atZone(java.time.ZoneId.systemDefault());
        java.time.ZonedDateTime zonedEndDate = endDate.atZone(java.time.ZoneId.systemDefault());
        long periodContent = contentRepository.findByCreatedAtBetween(zonedStartDate, zonedEndDate).size();
        
        // Get content by type
        Map<String, Long> contentByType = contentRepository.findByFamilyId(familyId)
                .stream()
                .collect(Collectors.groupingBy(
                        content -> content.getContentType().name(),
                        Collectors.counting()));
        
        analytics.put("totalContent", totalContent);
        analytics.put("periodContent", periodContent);
        analytics.put("contentByType", contentByType);
        analytics.put("period", period);
        analytics.put("startDate", startDate);
        analytics.put("endDate", endDate);
        
        return analytics;
    }
    
    // Private helper methods
    
    private Pageable buildPageable(SearchRequest request) {
        Sort sort = Sort.by(Sort.Direction.fromString(
                request.getSortDirection() != null ? request.getSortDirection() : "desc"),
                request.getSortBy() != null ? request.getSortBy() : "createdAt");
        
        return PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 10,
                sort);
    }
    
    private Page<LegacyContent> performTextSearch(SearchRequest request, Pageable pageable) {
        // Simple text search - can be enhanced with full-text search
        List<LegacyContent> results = contentRepository.findByTitleContainingIgnoreCase(request.getQuery());
        // Convert to page manually since repository doesn't have paginated version
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), results.size());
        List<LegacyContent> pageContent = results.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, results.size());
    }
    
    private Page<LegacyContent> performFilteredSearch(SearchRequest request, Pageable pageable) {
        // Filtered search without text query
        return contentRepository.findAllActive(pageable);
    }
    
    
    private ContentResponse convertToContentResponse(LegacyContent content) {
        // Use existing conversion logic from LegacyContentService
        return contentService.getContentById(content.getId());
    }
    
    
    private List<ContentResponse> getSimilarContentRecommendations(UUID userId, UUID familyId, Integer limit) {
        // TODO: Implement based on user's content preferences
        return getDefaultRecommendations(familyId, limit);
    }
    
    private List<ContentResponse> getTrendingContentRecommendations(UUID familyId, Integer limit) {
        List<LegacyContent> allContent = contentRepository.findByFamilyId(familyId);
        // Sort by creation date descending and limit
        allContent = allContent.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
        
        return allContent.stream()
                .map(this::convertToContentResponse)
                .collect(Collectors.toList());
    }
    
    private List<ContentResponse> getGenerationBasedRecommendations(UUID userId, UUID familyId, Integer limit) {
        // TODO: Implement based on generation level
        return getDefaultRecommendations(familyId, limit);
    }
    
    private List<ContentResponse> getCategoryBasedRecommendations(UUID userId, UUID familyId, Integer limit) {
        // TODO: Implement based on user's favorite categories
        return getDefaultRecommendations(familyId, limit);
    }
    
    private List<ContentResponse> getRecentActivityRecommendations(UUID familyId, Integer limit) {
        return getTrendingContentRecommendations(familyId, limit);
    }
    
    private List<ContentResponse> getDefaultRecommendations(UUID familyId, Integer limit) {
        List<LegacyContent> allContent = contentRepository.findByFamilyId(familyId);
        // Sort by creation date descending and limit
        allContent = allContent.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
        
        return allContent.stream()
                .map(this::convertToContentResponse)
                .collect(Collectors.toList());
    }
    
    private List<String> getPopularSearchTerms(Integer limit) {
        // TODO: Implement based on search history
        return List.of("family", "recipe", "story", "photo", "memory");
    }
    
    private List<String> getCategorySuggestions(String query, Integer limit) {
        // TODO: Implement category-based suggestions
        return List.of();
    }
}
