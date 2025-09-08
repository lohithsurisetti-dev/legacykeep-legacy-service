package com.legacykeep.legacy.controller;

import com.legacykeep.legacy.dto.request.CreateContentRequest;
import com.legacykeep.legacy.dto.response.ApiResponse;
import com.legacykeep.legacy.dto.response.ContentResponse;
import com.legacykeep.legacy.entity.LegacyContent;
import com.legacykeep.legacy.service.LegacyContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * REST Controller for managing legacy content.
 * 
 * Provides clean, meaningful endpoints for content management with pagination and filtering.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
@Slf4j
public class LegacyContentController {

    private final LegacyContentService contentService;

    /**
     * Create new content
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ContentResponse>> createContent(@Valid @RequestBody CreateContentRequest request) {
        log.info("Creating new content: {}", request.getTitle());
        ContentResponse response = contentService.createContent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Content created successfully"));
    }

    /**
     * Get content by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentResponse>> getContentById(@PathVariable UUID id) {
        log.debug("Getting content by ID: {}", id);
        ContentResponse response = contentService.getContentById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Content retrieved successfully"));
    }

    /**
     * Get content with pagination and optional filters
     * 
     * Query parameters:
     * - page: Page number (default: 0)
     * - size: Page size (default: 10)
     * - bucketId: Filter by bucket ID
     * - creatorId: Filter by creator ID
     * - familyId: Filter by family ID
     * - contentType: Filter by content type (TEXT, IMAGE, VIDEO, AUDIO, DOCUMENT)
     * - featured: Filter by featured status (true/false)
     * - sortBy: Sort field (createdAt, updatedAt, title)
     * - sortDir: Sort direction (asc, desc)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> getContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID bucketId,
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) UUID familyId,
            @RequestParam(required = false) LegacyContent.ContentType contentType,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting content with filters - page: {}, size: {}, bucketId: {}, creatorId: {}, contentType: {}, featured: {}", 
                page, size, bucketId, creatorId, contentType, featured);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentResponse> response = contentService.getContentWithFilters(
                pageable, bucketId, creatorId, familyId, contentType, featured, sortBy, sortDir);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Content retrieved successfully"));
    }

    /**
     * Search content by keyword with pagination
     * 
     * Query parameters:
     * - q: Search keyword
     * - page: Page number (default: 0)
     * - size: Page size (default: 10)
     * - bucketId: Filter by bucket ID (optional)
     * - creatorId: Filter by creator ID (optional)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> searchContent(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID bucketId,
            @RequestParam(required = false) UUID creatorId) {
        
        log.info("Searching content with keyword: '{}', page: {}, size: {}, bucketId: {}, creatorId: {}", 
                q, page, size, bucketId, creatorId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentResponse> response = contentService.searchContent(q, pageable, bucketId, creatorId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Search results retrieved successfully"));
    }

    /**
     * Update content
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentResponse>> updateContent(
            @PathVariable UUID id,
            @Valid @RequestBody CreateContentRequest request) {
        log.info("Updating content: {}", id);
        ContentResponse response = contentService.updateContent(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Content updated successfully"));
    }

    /**
     * Delete content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContent(@PathVariable UUID id) {
        log.info("Deleting content: {}", id);
        contentService.deleteContent(id);
        return ResponseEntity.ok(ApiResponse.success("Content deleted successfully"));
    }

    /**
     * Get content accessible to a specific user in a family
     * 
     * This endpoint returns content that the user has access to based on privacy settings
     * and recipient assignments.
     */
    @GetMapping("/accessible")
    public ResponseEntity<ApiResponse<Page<ContentResponse>>> getAccessibleContent(
            @RequestParam UUID userId,
            @RequestParam UUID familyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Getting accessible content for user: {} in family: {}, page: {}, size: {}", 
                userId, familyId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentResponse> response = contentService.getAccessibleContent(userId, familyId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Accessible content retrieved successfully"));
    }
}