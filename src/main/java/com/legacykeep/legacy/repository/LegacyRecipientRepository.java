package com.legacykeep.legacy.repository;

import com.legacykeep.legacy.entity.LegacyRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for LegacyRecipient entity
 * Handles database operations for targeted delivery system
 */
@Repository
public interface LegacyRecipientRepository extends JpaRepository<LegacyRecipient, UUID> {

    /**
     * Find recipients by content
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.contentId = :contentId ORDER BY r.createdAt")
    List<LegacyRecipient> findByContentId(@Param("contentId") UUID contentId);

    /**
     * Find recipients by recipient user
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.recipientId = :recipientId ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findByRecipientId(@Param("recipientId") UUID recipientId);

    /**
     * Find recipients by status
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.status = :status ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findByStatus(@Param("status") LegacyRecipient.RecipientStatus status);

    /**
     * Find recipients by access level
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.accessLevel = :accessLevel ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findByAccessLevel(@Param("accessLevel") LegacyRecipient.AccessLevel accessLevel);

    /**
     * Find recipients by recipient type
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.recipientType = :recipientType ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findByRecipientType(@Param("recipientType") LegacyRecipient.RecipientType recipientType);

    /**
     * Find recipient by content and recipient (unique constraint)
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.contentId = :contentId AND r.recipientId = :recipientId")
    Optional<LegacyRecipient> findByContentIdAndRecipientId(@Param("contentId") UUID contentId, @Param("recipientId") UUID recipientId);

    /**
     * Find pending recipients for a user
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.recipientId = :recipientId AND r.status = 'PENDING' ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findPendingRecipientsForUser(@Param("recipientId") UUID recipientId);

    /**
     * Find accepted recipients for a user
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.recipientId = :recipientId AND r.status = 'ACCEPTED' ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findAcceptedRecipientsForUser(@Param("recipientId") UUID recipientId);

    /**
     * Find rejected recipients for a user
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.recipientId = :recipientId AND r.status = 'REJECTED' ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findRejectedRecipientsForUser(@Param("recipientId") UUID recipientId);

    /**
     * Find recipients by relationship
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.recipientRelationship = :relationship ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findByRecipientRelationship(@Param("relationship") String relationship);

    /**
     * Find recipients by content and status
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.contentId = :contentId AND r.status = :status ORDER BY r.createdAt")
    List<LegacyRecipient> findByContentIdAndStatus(@Param("contentId") UUID contentId, @Param("status") LegacyRecipient.RecipientStatus status);

    /**
     * Find recipients by content and access level
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.contentId = :contentId AND r.accessLevel = :accessLevel ORDER BY r.createdAt")
    List<LegacyRecipient> findByContentIdAndAccessLevel(@Param("contentId") UUID contentId, @Param("accessLevel") LegacyRecipient.AccessLevel accessLevel);

    /**
     * Count recipients by content
     */
    @Query("SELECT COUNT(r) FROM LegacyRecipient r WHERE r.contentId = :contentId")
    long countByContentId(@Param("contentId") UUID contentId);

    /**
     * Count recipients by recipient user
     */
    @Query("SELECT COUNT(r) FROM LegacyRecipient r WHERE r.recipientId = :recipientId")
    long countByRecipientId(@Param("recipientId") UUID recipientId);

    /**
     * Count recipients by status
     */
    @Query("SELECT COUNT(r) FROM LegacyRecipient r WHERE r.status = :status")
    long countByStatus(@Param("status") LegacyRecipient.RecipientStatus status);

    /**
     * Count pending recipients for a user
     */
    @Query("SELECT COUNT(r) FROM LegacyRecipient r WHERE r.recipientId = :recipientId AND r.status = 'PENDING'")
    long countPendingRecipientsForUser(@Param("recipientId") UUID recipientId);

    /**
     * Count accepted recipients for a user
     */
    @Query("SELECT COUNT(r) FROM LegacyRecipient r WHERE r.recipientId = :recipientId AND r.status = 'ACCEPTED'")
    long countAcceptedRecipientsForUser(@Param("recipientId") UUID recipientId);

    /**
     * Find recipients with edit access
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.accessLevel = 'EDIT' AND r.status = 'ACCEPTED' ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findRecipientsWithEditAccess();

    /**
     * Find recipients with comment access
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE (r.accessLevel = 'COMMENT' OR r.accessLevel = 'EDIT') AND r.status = 'ACCEPTED' ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findRecipientsWithCommentAccess();

    /**
     * Find recipients by content creator (for content created by user)
     */
    @Query("SELECT r FROM LegacyRecipient r INNER JOIN LegacyContent c ON r.contentId = c.id WHERE c.creatorId = :creatorId ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findRecipientsByContentCreator(@Param("creatorId") UUID creatorId);

    /**
     * Find recipients by content creator and status
     */
    @Query("SELECT r FROM LegacyRecipient r INNER JOIN LegacyContent c ON r.contentId = c.id WHERE c.creatorId = :creatorId AND r.status = :status ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findRecipientsByContentCreatorAndStatus(@Param("creatorId") UUID creatorId, @Param("status") LegacyRecipient.RecipientStatus status);

    /**
     * Find recipients by content family
     */
    @Query("SELECT r FROM LegacyRecipient r INNER JOIN LegacyContent c ON r.contentId = c.id WHERE c.familyId = :familyId ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findRecipientsByContentFamily(@Param("familyId") UUID familyId);

    /**
     * Find recipients by content family and status
     */
    @Query("SELECT r FROM LegacyRecipient r INNER JOIN LegacyContent c ON r.contentId = c.id WHERE c.familyId = :familyId AND r.status = :status ORDER BY r.createdAt DESC")
    List<LegacyRecipient> findRecipientsByContentFamilyAndStatus(@Param("familyId") UUID familyId, @Param("status") LegacyRecipient.RecipientStatus status);

    /**
     * Find expired recipients (can be used for cleanup)
     */
    @Query("SELECT r FROM LegacyRecipient r WHERE r.status = 'PENDING' AND r.createdAt < :expiryDate")
    List<LegacyRecipient> findExpiredRecipients(@Param("expiryDate") java.time.ZonedDateTime expiryDate);

    /**
     * Check if recipient exists for content and user
     */
    @Query("SELECT COUNT(r) > 0 FROM LegacyRecipient r WHERE r.contentId = :contentId AND r.recipientId = :recipientId")
    boolean existsByContentIdAndRecipientId(@Param("contentId") UUID contentId, @Param("recipientId") UUID recipientId);
}
