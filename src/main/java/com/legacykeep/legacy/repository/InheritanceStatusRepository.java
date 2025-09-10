package com.legacykeep.legacy.repository;

import com.legacykeep.legacy.entity.InheritanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for InheritanceStatus entity.
 * Provides data access methods for inheritance status tracking.
 */
@Repository
public interface InheritanceStatusRepository extends JpaRepository<InheritanceStatus, UUID> {

    /**
     * Find inheritance status by content ID
     */
    List<InheritanceStatus> findByContentId(UUID contentId);

    /**
     * Find inheritance status by content ID with pagination
     */
    Page<InheritanceStatus> findByContentId(UUID contentId, Pageable pageable);

    /**
     * Find inheritance status by recipient ID
     */
    List<InheritanceStatus> findByRecipientId(UUID recipientId);

    /**
     * Find inheritance status by recipient ID with pagination
     */
    Page<InheritanceStatus> findByRecipientId(UUID recipientId, Pageable pageable);

    /**
     * Find inheritance status by inheritance rule ID
     */
    List<InheritanceStatus> findByInheritanceRuleId(UUID inheritanceRuleId);

    /**
     * Find inheritance status by inheritance rule ID with pagination
     */
    Page<InheritanceStatus> findByInheritanceRuleId(UUID inheritanceRuleId, Pageable pageable);

    /**
     * Find inheritance status by status
     */
    List<InheritanceStatus> findByStatus(InheritanceStatus.Status status);

    /**
     * Find inheritance status by status with pagination
     */
    Page<InheritanceStatus> findByStatus(InheritanceStatus.Status status, Pageable pageable);

    /**
     * Find inheritance status by recipient ID and status
     */
    List<InheritanceStatus> findByRecipientIdAndStatus(UUID recipientId, InheritanceStatus.Status status);

    /**
     * Find inheritance status by recipient ID and status with pagination
     */
    Page<InheritanceStatus> findByRecipientIdAndStatus(UUID recipientId, InheritanceStatus.Status status, Pageable pageable);

    /**
     * Find inheritance status by content ID and status
     */
    List<InheritanceStatus> findByContentIdAndStatus(UUID contentId, InheritanceStatus.Status status);

    /**
     * Find inheritance status by content ID and status with pagination
     */
    Page<InheritanceStatus> findByContentIdAndStatus(UUID contentId, InheritanceStatus.Status status, Pageable pageable);

    /**
     * Find inheritance status by inheritance rule ID and status
     */
    List<InheritanceStatus> findByInheritanceRuleIdAndStatus(UUID inheritanceRuleId, InheritanceStatus.Status status);

    /**
     * Find inheritance status by inheritance rule ID and status with pagination
     */
    Page<InheritanceStatus> findByInheritanceRuleIdAndStatus(UUID inheritanceRuleId, InheritanceStatus.Status status, Pageable pageable);

    /**
     * Find inheritance status by recipient ID and content ID
     */
    List<InheritanceStatus> findByRecipientIdAndContentId(UUID recipientId, UUID contentId);

    /**
     * Find inheritance status by recipient ID and content ID and inheritance rule ID
     */
    InheritanceStatus findByRecipientIdAndContentIdAndInheritanceRuleId(UUID recipientId, UUID contentId, UUID inheritanceRuleId);

    /**
     * Find inheritance status by relationship type ID
     */
    List<InheritanceStatus> findByRelationshipTypeId(Long relationshipTypeId);

    /**
     * Find inheritance status by relationship type ID with pagination
     */
    Page<InheritanceStatus> findByRelationshipTypeId(Long relationshipTypeId, Pageable pageable);

    /**
     * Find inheritance status by relationship type ID and status
     */
    List<InheritanceStatus> findByRelationshipTypeIdAndStatus(Long relationshipTypeId, InheritanceStatus.Status status);

    /**
     * Find inheritance status by relationship type ID and status with pagination
     */
    Page<InheritanceStatus> findByRelationshipTypeIdAndStatus(Long relationshipTypeId, InheritanceStatus.Status status, Pageable pageable);

    /**
     * Find inheritance status by inherited date range
     */
    List<InheritanceStatus> findByInheritedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inheritance status by inherited date range with pagination
     */
    Page<InheritanceStatus> findByInheritedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find inheritance status by accessed date range
     */
    List<InheritanceStatus> findByAccessedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inheritance status by accessed date range with pagination
     */
    Page<InheritanceStatus> findByAccessedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find inheritance status by declined date range
     */
    List<InheritanceStatus> findByDeclinedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inheritance status by declined date range with pagination
     */
    Page<InheritanceStatus> findByDeclinedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Count inheritance status by recipient ID
     */
    long countByRecipientId(UUID recipientId);

    /**
     * Count inheritance status by content ID
     */
    long countByContentId(UUID contentId);

    /**
     * Count inheritance status by inheritance rule ID
     */
    long countByInheritanceRuleId(UUID inheritanceRuleId);

    /**
     * Count inheritance status by status
     */
    long countByStatus(InheritanceStatus.Status status);

    /**
     * Count inheritance status by relationship type ID
     */
    long countByRelationshipTypeId(Long relationshipTypeId);

    /**
     * Count inheritance status by recipient ID and status
     */
    long countByRecipientIdAndStatus(UUID recipientId, InheritanceStatus.Status status);

    /**
     * Count inheritance status by content ID and status
     */
    long countByContentIdAndStatus(UUID contentId, InheritanceStatus.Status status);

    /**
     * Count inheritance status by inheritance rule ID and status
     */
    long countByInheritanceRuleIdAndStatus(UUID inheritanceRuleId, InheritanceStatus.Status status);

    /**
     * Count inheritance status by relationship type ID and status
     */
    long countByRelationshipTypeIdAndStatus(Long relationshipTypeId, InheritanceStatus.Status status);

    /**
     * Check if inheritance status exists by recipient ID and content ID and inheritance rule ID
     */
    boolean existsByRecipientIdAndContentIdAndInheritanceRuleId(UUID recipientId, UUID contentId, UUID inheritanceRuleId);

    /**
     * Find inheritance status by recipient ID and content ID and status
     */
    List<InheritanceStatus> findByRecipientIdAndContentIdAndStatus(UUID recipientId, UUID contentId, InheritanceStatus.Status status);

    /**
     * Find inheritance status by recipient ID and inheritance rule ID
     */
    List<InheritanceStatus> findByRecipientIdAndInheritanceRuleId(UUID recipientId, UUID inheritanceRuleId);

    /**
     * Find inheritance status by recipient ID and inheritance rule ID and status
     */
    List<InheritanceStatus> findByRecipientIdAndInheritanceRuleIdAndStatus(UUID recipientId, UUID inheritanceRuleId, InheritanceStatus.Status status);

    /**
     * Find inheritance status by content ID and inheritance rule ID
     */
    List<InheritanceStatus> findByContentIdAndInheritanceRuleId(UUID contentId, UUID inheritanceRuleId);

    /**
     * Find inheritance status by content ID and inheritance rule ID and status
     */
    List<InheritanceStatus> findByContentIdAndInheritanceRuleIdAndStatus(UUID contentId, UUID inheritanceRuleId, InheritanceStatus.Status status);

    /**
     * Find inheritance status by recipient ID and relationship type ID
     */
    List<InheritanceStatus> findByRecipientIdAndRelationshipTypeId(UUID recipientId, Long relationshipTypeId);

    /**
     * Find inheritance status by recipient ID and relationship type ID and status
     */
    List<InheritanceStatus> findByRecipientIdAndRelationshipTypeIdAndStatus(UUID recipientId, Long relationshipTypeId, InheritanceStatus.Status status);

    /**
     * Find inheritance status by content ID and relationship type ID
     */
    List<InheritanceStatus> findByContentIdAndRelationshipTypeId(UUID contentId, Long relationshipTypeId);

    /**
     * Find inheritance status by content ID and relationship type ID and status
     */
    List<InheritanceStatus> findByContentIdAndRelationshipTypeIdAndStatus(UUID contentId, Long relationshipTypeId, InheritanceStatus.Status status);

    /**
     * Find inheritance status by inheritance rule ID and relationship type ID
     */
    List<InheritanceStatus> findByInheritanceRuleIdAndRelationshipTypeId(UUID inheritanceRuleId, Long relationshipTypeId);

    /**
     * Find inheritance status by inheritance rule ID and relationship type ID and status
     */
    List<InheritanceStatus> findByInheritanceRuleIdAndRelationshipTypeIdAndStatus(UUID inheritanceRuleId, Long relationshipTypeId, InheritanceStatus.Status status);
}
