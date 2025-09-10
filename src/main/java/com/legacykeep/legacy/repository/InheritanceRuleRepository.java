package com.legacykeep.legacy.repository;

import com.legacykeep.legacy.entity.InheritanceRule;
import com.legacykeep.legacy.enums.InheritanceStatus;
import com.legacykeep.legacy.enums.InheritanceTrigger;
import com.legacykeep.legacy.enums.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for InheritanceRule entity.
 * Provides data access methods for inheritance rules.
 */
@Repository
public interface InheritanceRuleRepository extends JpaRepository<InheritanceRule, UUID> {

    /**
     * Find inheritance rules by content ID
     */
    List<InheritanceRule> findByContentId(UUID contentId);

    /**
     * Find inheritance rules by content ID with pagination
     */
    Page<InheritanceRule> findByContentId(UUID contentId, Pageable pageable);

    /**
     * Find inheritance rules by creator ID
     */
    List<InheritanceRule> findByCreatorId(UUID creatorId);

    /**
     * Find inheritance rules by creator ID with pagination
     */
    Page<InheritanceRule> findByCreatorId(UUID creatorId, Pageable pageable);

    /**
     * Find inheritance rules by status
     */
    List<InheritanceRule> findByStatus(InheritanceStatus status);

    /**
     * Find inheritance rules by status with pagination
     */
    Page<InheritanceRule> findByStatus(InheritanceStatus status, Pageable pageable);

    /**
     * Find inheritance rules by target type
     */
    List<InheritanceRule> findByTargetType(TargetType targetType);

    /**
     * Find inheritance rules by target type and value
     */
    List<InheritanceRule> findByTargetTypeAndTargetValue(TargetType targetType, String targetValue);

    /**
     * Find inheritance rules by inheritance trigger
     */
    List<InheritanceRule> findByInheritanceTrigger(InheritanceTrigger inheritanceTrigger);

    /**
     * Find active inheritance rules by target type and value
     */
    @Query("SELECT ir FROM InheritanceRule ir WHERE ir.targetType = :targetType AND ir.targetValue = :targetValue AND ir.status = 'ACTIVE'")
    List<InheritanceRule> findActiveByTargetTypeAndValue(@Param("targetType") TargetType targetType, @Param("targetValue") String targetValue);

    /**
     * Find inheritance rules by content ID and status
     */
    List<InheritanceRule> findByContentIdAndStatus(UUID contentId, InheritanceStatus status);

    /**
     * Find inheritance rules by creator ID and status
     */
    List<InheritanceRule> findByCreatorIdAndStatus(UUID creatorId, InheritanceStatus status);

    /**
     * Find inheritance rules by target type and status
     */
    List<InheritanceRule> findByTargetTypeAndStatus(TargetType targetType, InheritanceStatus status);

    /**
     * Find inheritance rules by inheritance trigger and status
     */
    List<InheritanceRule> findByInheritanceTriggerAndStatus(InheritanceTrigger inheritanceTrigger, InheritanceStatus status);

    /**
     * Find inheritance rules by priority (higher priority first)
     */
    List<InheritanceRule> findByStatusOrderByPriorityDesc(InheritanceStatus status);

    /**
     * Find inheritance rules by content ID and target type
     */
    List<InheritanceRule> findByContentIdAndTargetType(UUID contentId, TargetType targetType);

    /**
     * Find inheritance rules by content ID and target value
     */
    List<InheritanceRule> findByContentIdAndTargetValue(UUID contentId, String targetValue);

    /**
     * Find inheritance rules by content ID and inheritance trigger
     */
    List<InheritanceRule> findByContentIdAndInheritanceTrigger(UUID contentId, InheritanceTrigger inheritanceTrigger);

    /**
     * Count inheritance rules by content ID
     */
    long countByContentId(UUID contentId);

    /**
     * Count inheritance rules by creator ID
     */
    long countByCreatorId(UUID creatorId);

    /**
     * Count inheritance rules by status
     */
    long countByStatus(InheritanceStatus status);

    /**
     * Count inheritance rules by target type
     */
    long countByTargetType(TargetType targetType);

    /**
     * Count inheritance rules by inheritance trigger
     */
    long countByInheritanceTrigger(InheritanceTrigger inheritanceTrigger);

    /**
     * Check if inheritance rule exists by content ID and target type and value
     */
    boolean existsByContentIdAndTargetTypeAndTargetValue(UUID contentId, TargetType targetType, String targetValue);

    /**
     * Find inheritance rules by content ID and target type and value
     */
    List<InheritanceRule> findByContentIdAndTargetTypeAndTargetValue(UUID contentId, TargetType targetType, String targetValue);

    /**
     * Find inheritance rules by creator ID and target type
     */
    List<InheritanceRule> findByCreatorIdAndTargetType(UUID creatorId, TargetType targetType);

    /**
     * Find inheritance rules by creator ID and target value
     */
    List<InheritanceRule> findByCreatorIdAndTargetValue(UUID creatorId, String targetValue);

    /**
     * Find inheritance rules by creator ID and inheritance trigger
     */
    List<InheritanceRule> findByCreatorIdAndInheritanceTrigger(UUID creatorId, InheritanceTrigger inheritanceTrigger);

    /**
     * Find inheritance rules by target type and target value and status
     */
    List<InheritanceRule> findByTargetTypeAndTargetValueAndStatus(TargetType targetType, String targetValue, InheritanceStatus status);

    /**
     * Find inheritance rules by target type and target value and inheritance trigger
     */
    List<InheritanceRule> findByTargetTypeAndTargetValueAndInheritanceTrigger(TargetType targetType, String targetValue, InheritanceTrigger inheritanceTrigger);

    /**
     * Find inheritance rules by target type and target value and inheritance trigger and status
     */
    List<InheritanceRule> findByTargetTypeAndTargetValueAndInheritanceTriggerAndStatus(TargetType targetType, String targetValue, InheritanceTrigger inheritanceTrigger, InheritanceStatus status);
}
