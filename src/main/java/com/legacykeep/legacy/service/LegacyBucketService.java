package com.legacykeep.legacy.service;

import com.legacykeep.legacy.dto.request.CreateBucketRequest;
import com.legacykeep.legacy.dto.response.BucketResponse;
import com.legacykeep.legacy.entity.LegacyBucket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for managing legacy buckets.
 * 
 * Provides business logic for bucket management with clean, meaningful operations.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public interface LegacyBucketService {

    /**
     * Create a new bucket
     */
    BucketResponse createBucket(CreateBucketRequest request);

    /**
     * Get bucket by ID
     */
    BucketResponse getBucketById(UUID id);

    /**
     * Get buckets with pagination and filters
     */
    Page<BucketResponse> getBucketsWithFilters(
            Pageable pageable,
            UUID creatorId,
            UUID familyId,
            UUID categoryId,
            LegacyBucket.BucketType bucketType,
            Boolean featured,
            String sortBy,
            String sortDir);

    /**
     * Search buckets by keyword with pagination and filters
     */
    Page<BucketResponse> searchBuckets(
            String keyword,
            Pageable pageable,
            UUID creatorId,
            UUID categoryId);

    /**
     * Update bucket
     */
    BucketResponse updateBucket(UUID id, CreateBucketRequest request);

    /**
     * Delete bucket
     */
    void deleteBucket(UUID id);

    /**
     * Get buckets accessible to a specific user in a family
     */
    Page<BucketResponse> getAccessibleBuckets(UUID userId, UUID familyId, Pageable pageable);
}