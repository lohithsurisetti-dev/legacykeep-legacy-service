package com.legacykeep.legacy.service;

import com.legacykeep.legacy.dto.request.CreateContentRequest;
import com.legacykeep.legacy.dto.response.ContentResponse;
import com.legacykeep.legacy.entity.LegacyContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for managing legacy content.
 * 
 * Provides business logic for content management with clean, meaningful operations.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public interface LegacyContentService {

    /**
     * Create new content
     */
    ContentResponse createContent(CreateContentRequest request);

    /**
     * Get content by ID
     */
    ContentResponse getContentById(UUID id);

    /**
     * Get content with pagination and filters
     */
    Page<ContentResponse> getContentWithFilters(
            Pageable pageable,
            UUID bucketId,
            UUID creatorId,
            UUID familyId,
            LegacyContent.ContentType contentType,
            Boolean featured,
            String sortBy,
            String sortDir);

    /**
     * Search content by keyword with pagination and filters
     */
    Page<ContentResponse> searchContent(
            String keyword,
            Pageable pageable,
            UUID bucketId,
            UUID creatorId);

    /**
     * Update content
     */
    ContentResponse updateContent(UUID id, CreateContentRequest request);

    /**
     * Delete content
     */
    void deleteContent(UUID id);

    /**
     * Get content accessible to a specific user in a family
     */
    Page<ContentResponse> getAccessibleContent(UUID userId, UUID familyId, Pageable pageable);
}