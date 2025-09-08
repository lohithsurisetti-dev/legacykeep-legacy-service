package com.legacykeep.legacy.repository;

import com.legacykeep.legacy.entity.LegacyBucket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for LegacyBucket entity
 * Handles database operations for user-created buckets
 */
@Repository
public interface LegacyBucketRepository extends JpaRepository<LegacyBucket, UUID> {

    /**
     * Find buckets by creator
     */
    @Query("SELECT b FROM LegacyBucket b WHERE b.creatorId = :creatorId ORDER BY b.sortOrder, b.name")
    List<LegacyBucket> findByCreatorId(@Param("creatorId") UUID creatorId);

    /**
     * Find buckets by family
     */
    @Query("SELECT b FROM LegacyBucket b WHERE b.familyId = :familyId ORDER BY b.sortOrder, b.name")
    List<LegacyBucket> findByFamilyId(@Param("familyId") UUID familyId);

    /**
     * Find buckets by category
     */
    @Query("SELECT b FROM LegacyBucket b WHERE b.categoryId = :categoryId ORDER BY b.sortOrder, b.name")
    List<LegacyBucket> findByCategoryId(@Param("categoryId") UUID categoryId);

    /**
     * Find buckets by creator and category
     */
    @Query("SELECT b FROM LegacyBucket b WHERE b.creatorId = :creatorId AND b.categoryId = :categoryId ORDER BY b.sortOrder, b.name")
    List<LegacyBucket> findByCreatorIdAndCategoryId(@Param("creatorId") UUID creatorId, @Param("categoryId") UUID categoryId);

    /**
     * Find featured buckets
     */
    @Query("SELECT b FROM LegacyBucket b WHERE b.isFeatured = true ORDER BY b.sortOrder, b.name")
    List<LegacyBucket> findFeaturedBuckets();

    /**
     * Find featured buckets by creator
     */
    @Query("SELECT b FROM LegacyBucket b WHERE b.creatorId = :creatorId AND b.isFeatured = true ORDER BY b.sortOrder, b.name")
    List<LegacyBucket> findFeaturedBucketsByCreator(@Param("creatorId") UUID creatorId);

    /**
     * Find buckets by type
     */
    @Query("SELECT b FROM LegacyBucket b WHERE b.bucketType = :type ORDER BY b.sortOrder, b.name")
    List<LegacyBucket> findByBucketType(@Param("type") LegacyBucket.BucketType type);

    /**
     * Find buckets by privacy level
     */
    @Query("SELECT b FROM LegacyBucket b WHERE b.privacyLevel = :privacyLevel ORDER BY b.sortOrder, b.name")
    List<LegacyBucket> findByPrivacyLevel(@Param("privacyLevel") LegacyBucket.PrivacyLevel privacyLevel);

    /**
     * Find bucket by name and creator (for uniqueness check)
     */
    @Query("SELECT b FROM LegacyBucket b WHERE LOWER(b.name) = LOWER(:name) AND b.creatorId = :creatorId")
    Optional<LegacyBucket> findByNameAndCreatorId(@Param("name") String name, @Param("creatorId") UUID creatorId);

    /**
     * Find buckets by name containing (case insensitive)
     */
    @Query("SELECT b FROM LegacyBucket b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY b.sortOrder, b.name")
    List<LegacyBucket> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find buckets by creator with name containing
     */
    @Query("SELECT b FROM LegacyBucket b WHERE b.creatorId = :creatorId AND LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY b.sortOrder, b.name")
    List<LegacyBucket> findByCreatorIdAndNameContainingIgnoreCase(@Param("creatorId") UUID creatorId, @Param("name") String name);

    /**
     * Count buckets by creator
     */
    @Query("SELECT COUNT(b) FROM LegacyBucket b WHERE b.creatorId = :creatorId")
    long countByCreatorId(@Param("creatorId") UUID creatorId);

    /**
     * Count buckets by category
     */
    @Query("SELECT COUNT(b) FROM LegacyBucket b WHERE b.categoryId = :categoryId")
    long countByCategoryId(@Param("categoryId") UUID categoryId);

    /**
     * Find buckets that can be deleted (no content)
     */
    @Query("SELECT b FROM LegacyBucket b WHERE b.id NOT IN (SELECT DISTINCT c.bucketId FROM LegacyContent c)")
    List<LegacyBucket> findDeletableBuckets();

    /**
     * Find buckets accessible to user based on privacy
     */
    @Query("SELECT b FROM LegacyBucket b WHERE " +
           "(b.privacyLevel = 'PUBLIC') OR " +
           "(b.privacyLevel = 'FAMILY' AND b.familyId = :familyId) OR " +
           "(b.privacyLevel = 'EXTENDED_FAMILY' AND b.familyId = :familyId) OR " +
           "(b.creatorId = :userId) " +
           "ORDER BY b.sortOrder, b.name")
    List<LegacyBucket> findAccessibleBuckets(@Param("userId") UUID userId, @Param("familyId") UUID familyId);

    /**
     * Find recent buckets by creator
     */
    @Query("SELECT b FROM LegacyBucket b WHERE b.creatorId = :creatorId ORDER BY b.createdAt DESC")
    List<LegacyBucket> findRecentBucketsByCreator(@Param("creatorId") UUID creatorId);

    /**
     * Find buckets with content count
     */
    @Query("SELECT b, COUNT(c) as contentCount FROM LegacyBucket b LEFT JOIN LegacyContent c ON b.id = c.bucketId " +
           "WHERE b.creatorId = :creatorId GROUP BY b.id ORDER BY b.sortOrder, b.name")
    List<Object[]> findBucketsWithContentCount(@Param("creatorId") UUID creatorId);
}
