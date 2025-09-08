package com.legacykeep.legacy.service.impl;

import com.legacykeep.legacy.dto.request.CreateBucketRequest;
import com.legacykeep.legacy.dto.response.BucketResponse;
import com.legacykeep.legacy.entity.LegacyBucket;
import com.legacykeep.legacy.repository.LegacyBucketRepository;
import com.legacykeep.legacy.repository.LegacyCategoryRepository;
import com.legacykeep.legacy.service.LegacyBucketService;
import com.legacykeep.legacy.service.LegacyCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of LegacyBucketService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LegacyBucketServiceImpl implements LegacyBucketService {

    private final LegacyBucketRepository bucketRepository;
    private final LegacyCategoryRepository categoryRepository;
    private final LegacyCategoryService categoryService;

    @Override
    public Page<BucketResponse> getBucketsWithFilters(
            Pageable pageable,
            UUID creatorId,
            UUID familyId,
            UUID categoryId,
            LegacyBucket.BucketType bucketType,
            Boolean featured,
            String sortBy,
            String sortDir) {
        
        log.info("Fetching buckets with filters - creatorId: {}, familyId: {}, categoryId: {}, bucketType: {}, featured: {}", 
                creatorId, familyId, categoryId, bucketType, featured);
        
        // For now, return all buckets with basic pagination
        // TODO: Implement proper filtering logic
        Page<LegacyBucket> bucketPage = bucketRepository.findAll(pageable);
        return bucketPage.map(this::mapToResponse);
    }

    @Override
    public BucketResponse createBucket(CreateBucketRequest request) {
        log.info("Creating new bucket: {} for creator: {}", request.getName(), request.getCreatorId());
        
        // Validate category exists if provided
        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + request.getCategoryId()));
        }

        LegacyBucket bucket = LegacyBucket.builder()
                .name(request.getName())
                .description(request.getDescription())
                .creatorId(request.getCreatorId())
                .familyId(request.getFamilyId())
                .categoryId(request.getCategoryId())
                .bucketType(request.getBucketType())
                .privacyLevel(request.getPrivacyLevel())
                .isFeatured(request.getIsFeatured())
                .sortOrder(request.getSortOrder())
                .build();

        LegacyBucket savedBucket = bucketRepository.save(bucket);
        log.info("Created bucket with ID: {}", savedBucket.getId());

        return mapToResponse(savedBucket);
    }

    @Override
    public Page<BucketResponse> searchBuckets(String keyword, Pageable pageable, UUID creatorId, UUID categoryId) {
        log.info("Searching buckets with keyword: '{}', creatorId: {}, categoryId: {}", keyword, creatorId, categoryId);
        
        // For now, return all buckets with basic pagination
        // TODO: Implement proper search logic
        Page<LegacyBucket> bucketPage = bucketRepository.findAll(pageable);
        return bucketPage.map(this::mapToResponse);
    }

    @Override
    public BucketResponse updateBucket(UUID id, CreateBucketRequest request) {
        log.info("Updating bucket: {}", id);
        
        LegacyBucket existingBucket = bucketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bucket not found with ID: " + id));
        
        // Update fields
        existingBucket.setName(request.getName());
        existingBucket.setDescription(request.getDescription());
        existingBucket.setCategoryId(request.getCategoryId());
        existingBucket.setBucketType(request.getBucketType());
        existingBucket.setPrivacyLevel(request.getPrivacyLevel());
        existingBucket.setIsFeatured(request.getIsFeatured());
        existingBucket.setSortOrder(request.getSortOrder());
        
        LegacyBucket updatedBucket = bucketRepository.save(existingBucket);
        return mapToResponse(updatedBucket);
    }

    @Override
    public void deleteBucket(UUID id) {
        log.info("Deleting bucket: {}", id);
        
        LegacyBucket bucket = bucketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bucket not found with ID: " + id));
        
        bucketRepository.delete(bucket);
    }

    @Override
    public Page<BucketResponse> getAccessibleBuckets(UUID userId, UUID familyId, Pageable pageable) {
        log.info("Getting accessible buckets for user: {} in family: {}", userId, familyId);
        
        // For now, return all buckets with basic pagination
        // TODO: Implement proper access control logic
        Page<LegacyBucket> bucketPage = bucketRepository.findAll(pageable);
        return bucketPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BucketResponse getBucketById(UUID id) {
        log.info("Fetching bucket by ID: {}", id);
        LegacyBucket bucket = bucketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bucket not found with ID: " + id));
        return mapToResponse(bucket);
    }

    private BucketResponse mapToResponse(LegacyBucket bucket) {
        return BucketResponse.builder()
                .id(bucket.getId())
                .name(bucket.getName())
                .description(bucket.getDescription())
                .creatorId(bucket.getCreatorId())
                .familyId(bucket.getFamilyId())
                .categoryId(bucket.getCategoryId())
                .bucketType(bucket.getBucketType())
                .privacyLevel(bucket.getPrivacyLevel())
                .isFeatured(bucket.getIsFeatured())
                .sortOrder(bucket.getSortOrder())
                .createdAt(bucket.getCreatedAt())
                .updatedAt(bucket.getUpdatedAt())
                .build();
    }
}