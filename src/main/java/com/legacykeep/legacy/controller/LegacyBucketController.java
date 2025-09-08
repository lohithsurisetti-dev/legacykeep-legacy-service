package com.legacykeep.legacy.controller;

import com.legacykeep.legacy.dto.request.CreateBucketRequest;
import com.legacykeep.legacy.dto.response.ApiResponse;
import com.legacykeep.legacy.dto.response.BucketResponse;
import com.legacykeep.legacy.entity.LegacyBucket;
import com.legacykeep.legacy.service.LegacyBucketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

/**
 * REST Controller for managing legacy buckets.
 * 
 * Provides clean, meaningful endpoints for bucket management with pagination and filtering.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/buckets")
@RequiredArgsConstructor
@Slf4j
public class LegacyBucketController {

    private final LegacyBucketService bucketService;

    /**
     * Create a new bucket
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BucketResponse>> createBucket(@Valid @RequestBody CreateBucketRequest request) {
        log.info("Creating new bucket: {}", request.getName());
        BucketResponse response = bucketService.createBucket(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Bucket created successfully"));
    }

    /**
     * Get bucket by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BucketResponse>> getBucketById(@PathVariable UUID id) {
        log.debug("Getting bucket by ID: {}", id);
        BucketResponse response = bucketService.getBucketById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Bucket retrieved successfully"));
    }

    /**
     * Get buckets with pagination and optional filters
     * 
     * Query parameters:
     * - page: Page number (default: 0)
     * - size: Page size (default: 10)
     * - creatorId: Filter by creator ID
     * - familyId: Filter by family ID
     * - categoryId: Filter by category ID
     * - bucketType: Filter by bucket type (CUSTOM, SYSTEM)
     * - featured: Filter by featured status (true/false)
     * - sortBy: Sort field (createdAt, updatedAt, name)
     * - sortDir: Sort direction (asc, desc)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BucketResponse>>> getBuckets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) UUID familyId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) LegacyBucket.BucketType bucketType,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting buckets with filters - page: {}, size: {}, creatorId: {}, familyId: {}, categoryId: {}, bucketType: {}, featured: {}", 
                page, size, creatorId, familyId, categoryId, bucketType, featured);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BucketResponse> response = bucketService.getBucketsWithFilters(
                pageable, creatorId, familyId, categoryId, bucketType, featured, sortBy, sortDir);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Buckets retrieved successfully"));
    }

    /**
     * Search buckets by keyword with pagination
     * 
     * Query parameters:
     * - q: Search keyword
     * - page: Page number (default: 0)
     * - size: Page size (default: 10)
     * - creatorId: Filter by creator ID (optional)
     * - categoryId: Filter by category ID (optional)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BucketResponse>>> searchBuckets(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) UUID categoryId) {
        
        log.info("Searching buckets with keyword: '{}', page: {}, size: {}, creatorId: {}, categoryId: {}", 
                q, page, size, creatorId, categoryId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BucketResponse> response = bucketService.searchBuckets(q, pageable, creatorId, categoryId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Search results retrieved successfully"));
    }

    /**
     * Update bucket
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BucketResponse>> updateBucket(
            @PathVariable UUID id,
            @Valid @RequestBody CreateBucketRequest request) {
        log.info("Updating bucket: {}", id);
        BucketResponse response = bucketService.updateBucket(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Bucket updated successfully"));
    }

    /**
     * Delete bucket
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBucket(@PathVariable UUID id) {
        log.info("Deleting bucket: {}", id);
        bucketService.deleteBucket(id);
        return ResponseEntity.ok(ApiResponse.success("Bucket deleted successfully"));
    }

    /**
     * Get buckets accessible to a specific user in a family
     * 
     * This endpoint returns buckets that the user has access to based on privacy settings.
     */
    @GetMapping("/accessible")
    public ResponseEntity<ApiResponse<Page<BucketResponse>>> getAccessibleBuckets(
            @RequestParam UUID userId,
            @RequestParam UUID familyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Getting accessible buckets for user: {} in family: {}, page: {}, size: {}", 
                userId, familyId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BucketResponse> response = bucketService.getAccessibleBuckets(userId, familyId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Accessible buckets retrieved successfully"));
    }
}