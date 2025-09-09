package com.legacykeep.legacy.repository;

import com.legacykeep.legacy.entity.LegacyContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for LegacyContent entity
 * Handles database operations for legacy content items
 */
@Repository
public interface LegacyContentRepository extends JpaRepository<LegacyContent, UUID> {

    /**
     * Find content by bucket
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.bucketId = :bucketId AND c.status != 'DELETED' AND c.status != 'DELETED' ORDER BY c.sortOrder, c.title")
    List<LegacyContent> findByBucketId(@Param("bucketId") UUID bucketId);

    /**
     * Find content by bucket with pagination
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.bucketId = :bucketId AND c.status != 'DELETED' AND c.status != 'DELETED' ORDER BY c.sortOrder, c.title")
    Page<LegacyContent> findByBucketId(@Param("bucketId") UUID bucketId, Pageable pageable);

    /**
     * Find content by creator
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.creatorId = :creatorId ORDER BY c.createdAt DESC")
    List<LegacyContent> findByCreatorId(@Param("creatorId") UUID creatorId);

    /**
     * Find content by creator with pagination
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.creatorId = :creatorId ORDER BY c.createdAt DESC")
    Page<LegacyContent> findByCreatorId(@Param("creatorId") UUID creatorId, Pageable pageable);

    /**
     * Find content by family
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.familyId = :familyId ORDER BY c.createdAt DESC")
    List<LegacyContent> findByFamilyId(@Param("familyId") UUID familyId);

    /**
     * Find content by content type
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.contentType = :contentType ORDER BY c.createdAt DESC")
    List<LegacyContent> findByContentType(@Param("contentType") LegacyContent.ContentType contentType);

    /**
     * Find content by status
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.status = :status ORDER BY c.createdAt DESC")
    List<LegacyContent> findByStatus(@Param("status") LegacyContent.ContentStatus status);

    /**
     * Find featured content
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.isFeatured = true ORDER BY c.sortOrder, c.createdAt DESC")
    List<LegacyContent> findFeaturedContent();

    /**
     * Find featured content by creator
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.creatorId = :creatorId AND c.isFeatured = true ORDER BY c.sortOrder, c.createdAt DESC")
    List<LegacyContent> findFeaturedContentByCreator(@Param("creatorId") UUID creatorId);

    /**
     * Find content by title containing (case insensitive)
     */
    @Query("SELECT c FROM LegacyContent c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY c.createdAt DESC")
    List<LegacyContent> findByTitleContainingIgnoreCase(@Param("title") String title);

    /**
     * Find content by creator and title containing
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.creatorId = :creatorId AND LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY c.createdAt DESC")
    List<LegacyContent> findByCreatorIdAndTitleContainingIgnoreCase(@Param("creatorId") UUID creatorId, @Param("title") String title);

    /**
     * Find content by bucket and title containing
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.bucketId = :bucketId AND LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY c.sortOrder, c.createdAt DESC")
    List<LegacyContent> findByBucketIdAndTitleContainingIgnoreCase(@Param("bucketId") UUID bucketId, @Param("title") String title);

    /**
     * Find content by generation level
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.generationLevel = :generationLevel ORDER BY c.createdAt DESC")
    List<LegacyContent> findByGenerationLevel(@Param("generationLevel") Integer generationLevel);

    /**
     * Find content by privacy level
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.privacyLevel = :privacyLevel ORDER BY c.createdAt DESC")
    List<LegacyContent> findByPrivacyLevel(@Param("privacyLevel") LegacyContent.PrivacyLevel privacyLevel);

    /**
     * Find content created between dates
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.createdAt BETWEEN :startDate AND :endDate ORDER BY c.createdAt DESC")
    List<LegacyContent> findByCreatedAtBetween(@Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

    /**
     * Find content by creator between dates
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.creatorId = :creatorId AND c.createdAt BETWEEN :startDate AND :endDate ORDER BY c.createdAt DESC")
    List<LegacyContent> findByCreatorIdAndCreatedAtBetween(@Param("creatorId") UUID creatorId, @Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);

    /**
     * Count content by creator
     */
    @Query("SELECT COUNT(c) FROM LegacyContent c WHERE c.creatorId = :creatorId")
    long countByCreatorId(@Param("creatorId") UUID creatorId);

    /**
     * Count content by bucket
     */
    @Query("SELECT COUNT(c) FROM LegacyContent c WHERE c.bucketId = :bucketId")
    long countByBucketId(@Param("bucketId") UUID bucketId);

    /**
     * Count content by content type
     */
    @Query("SELECT COUNT(c) FROM LegacyContent c WHERE c.contentType = :contentType")
    long countByContentType(@Param("contentType") LegacyContent.ContentType contentType);

    /**
     * Find content accessible to user based on privacy
     */
    @Query("SELECT c FROM LegacyContent c WHERE " +
           "(c.privacyLevel = 'PUBLIC') OR " +
           "(c.privacyLevel = 'FAMILY' AND c.familyId = :familyId) OR " +
           "(c.privacyLevel = 'EXTENDED_FAMILY' AND c.familyId = :familyId) OR " +
           "(c.creatorId = :userId) " +
           "ORDER BY c.createdAt DESC")
    List<LegacyContent> findAccessibleContent(@Param("userId") UUID userId, @Param("familyId") UUID familyId);

    /**
     * Find content accessible to user with pagination
     */
    @Query("SELECT c FROM LegacyContent c WHERE " +
           "(c.privacyLevel = 'PUBLIC') OR " +
           "(c.privacyLevel = 'FAMILY' AND c.familyId = :familyId) OR " +
           "(c.privacyLevel = 'EXTENDED_FAMILY' AND c.familyId = :familyId) OR " +
           "(c.creatorId = :userId) " +
           "ORDER BY c.createdAt DESC")
    Page<LegacyContent> findAccessibleContent(@Param("userId") UUID userId, @Param("familyId") UUID familyId, Pageable pageable);

    /**
     * Find recent content by creator
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.creatorId = :creatorId ORDER BY c.createdAt DESC")
    List<LegacyContent> findRecentContentByCreator(@Param("creatorId") UUID creatorId);

    /**
     * Find content with media files
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.id IN (SELECT DISTINCT m.contentId FROM LegacyMediaFile m) ORDER BY c.createdAt DESC")
    List<LegacyContent> findContentWithMediaFiles();

    /**
     * Find content without media files
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.id NOT IN (SELECT DISTINCT m.contentId FROM LegacyMediaFile m) ORDER BY c.createdAt DESC")
    List<LegacyContent> findContentWithoutMediaFiles();

    /**
     * Find content by bucket and content type
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.bucketId = :bucketId AND c.contentType = :contentType ORDER BY c.sortOrder, c.createdAt DESC")
    List<LegacyContent> findByBucketIdAndContentType(@Param("bucketId") UUID bucketId, @Param("contentType") LegacyContent.ContentType contentType);

    /**
     * Find content by creator and content type
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.creatorId = :creatorId AND c.contentType = :contentType ORDER BY c.createdAt DESC")
    List<LegacyContent> findByCreatorIdAndContentType(@Param("creatorId") UUID creatorId, @Param("contentType") LegacyContent.ContentType contentType);

    /**
     * Find all active content (not deleted) with pagination
     */
    @Query("SELECT c FROM LegacyContent c WHERE c.status != 'DELETED' ORDER BY c.sortOrder, c.title")
    Page<LegacyContent> findAllActive(Pageable pageable);
}
