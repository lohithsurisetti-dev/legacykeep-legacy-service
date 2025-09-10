package com.legacykeep.legacy.repository;

import com.legacykeep.legacy.entity.InheritanceEvent;
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
 * Repository interface for InheritanceEvent entity.
 * Provides data access methods for inheritance event tracking.
 */
@Repository
public interface InheritanceEventRepository extends JpaRepository<InheritanceEvent, UUID> {

    /**
     * Find inheritance events by inheritance rule ID
     */
    List<InheritanceEvent> findByInheritanceRuleId(UUID inheritanceRuleId);

    /**
     * Find inheritance events by inheritance rule ID with pagination
     */
    Page<InheritanceEvent> findByInheritanceRuleId(UUID inheritanceRuleId, Pageable pageable);

    /**
     * Find inheritance events by event type
     */
    List<InheritanceEvent> findByEventType(InheritanceEvent.EventType eventType);

    /**
     * Find inheritance events by event type with pagination
     */
    Page<InheritanceEvent> findByEventType(InheritanceEvent.EventType eventType, Pageable pageable);

    /**
     * Find inheritance events by created by
     */
    List<InheritanceEvent> findByCreatedBy(UUID createdBy);

    /**
     * Find inheritance events by created by with pagination
     */
    Page<InheritanceEvent> findByCreatedBy(UUID createdBy, Pageable pageable);

    /**
     * Find inheritance events by inheritance rule ID and event type
     */
    List<InheritanceEvent> findByInheritanceRuleIdAndEventType(UUID inheritanceRuleId, InheritanceEvent.EventType eventType);

    /**
     * Find inheritance events by inheritance rule ID and event type with pagination
     */
    Page<InheritanceEvent> findByInheritanceRuleIdAndEventType(UUID inheritanceRuleId, InheritanceEvent.EventType eventType, Pageable pageable);

    /**
     * Find inheritance events by created by and event type
     */
    List<InheritanceEvent> findByCreatedByAndEventType(UUID createdBy, InheritanceEvent.EventType eventType);

    /**
     * Find inheritance events by created by and event type with pagination
     */
    Page<InheritanceEvent> findByCreatedByAndEventType(UUID createdBy, InheritanceEvent.EventType eventType, Pageable pageable);

    /**
     * Find inheritance events by created date range
     */
    List<InheritanceEvent> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inheritance events by created date range with pagination
     */
    Page<InheritanceEvent> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find inheritance events by inheritance rule ID and created date range
     */
    List<InheritanceEvent> findByInheritanceRuleIdAndCreatedAtBetween(UUID inheritanceRuleId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inheritance events by inheritance rule ID and created date range with pagination
     */
    Page<InheritanceEvent> findByInheritanceRuleIdAndCreatedAtBetween(UUID inheritanceRuleId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find inheritance events by event type and created date range
     */
    List<InheritanceEvent> findByEventTypeAndCreatedAtBetween(InheritanceEvent.EventType eventType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inheritance events by event type and created date range with pagination
     */
    Page<InheritanceEvent> findByEventTypeAndCreatedAtBetween(InheritanceEvent.EventType eventType, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find inheritance events by created by and created date range
     */
    List<InheritanceEvent> findByCreatedByAndCreatedAtBetween(UUID createdBy, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inheritance events by created by and created date range with pagination
     */
    Page<InheritanceEvent> findByCreatedByAndCreatedAtBetween(UUID createdBy, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Count inheritance events by inheritance rule ID
     */
    long countByInheritanceRuleId(UUID inheritanceRuleId);

    /**
     * Count inheritance events by event type
     */
    long countByEventType(InheritanceEvent.EventType eventType);

    /**
     * Count inheritance events by created by
     */
    long countByCreatedBy(UUID createdBy);

    /**
     * Count inheritance events by inheritance rule ID and event type
     */
    long countByInheritanceRuleIdAndEventType(UUID inheritanceRuleId, InheritanceEvent.EventType eventType);

    /**
     * Count inheritance events by created by and event type
     */
    long countByCreatedByAndEventType(UUID createdBy, InheritanceEvent.EventType eventType);

    /**
     * Count inheritance events by created date range
     */
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Count inheritance events by inheritance rule ID and created date range
     */
    long countByInheritanceRuleIdAndCreatedAtBetween(UUID inheritanceRuleId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Count inheritance events by event type and created date range
     */
    long countByEventTypeAndCreatedAtBetween(InheritanceEvent.EventType eventType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Count inheritance events by created by and created date range
     */
    long countByCreatedByAndCreatedAtBetween(UUID createdBy, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inheritance events by inheritance rule ID and event type and created date range
     */
    List<InheritanceEvent> findByInheritanceRuleIdAndEventTypeAndCreatedAtBetween(UUID inheritanceRuleId, InheritanceEvent.EventType eventType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inheritance events by inheritance rule ID and event type and created date range with pagination
     */
    Page<InheritanceEvent> findByInheritanceRuleIdAndEventTypeAndCreatedAtBetween(UUID inheritanceRuleId, InheritanceEvent.EventType eventType, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find inheritance events by created by and event type and created date range
     */
    List<InheritanceEvent> findByCreatedByAndEventTypeAndCreatedAtBetween(UUID createdBy, InheritanceEvent.EventType eventType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inheritance events by created by and event type and created date range with pagination
     */
    Page<InheritanceEvent> findByCreatedByAndEventTypeAndCreatedAtBetween(UUID createdBy, InheritanceEvent.EventType eventType, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find inheritance events by inheritance rule ID and created by
     */
    List<InheritanceEvent> findByInheritanceRuleIdAndCreatedBy(UUID inheritanceRuleId, UUID createdBy);

    /**
     * Find inheritance events by inheritance rule ID and created by with pagination
     */
    Page<InheritanceEvent> findByInheritanceRuleIdAndCreatedBy(UUID inheritanceRuleId, UUID createdBy, Pageable pageable);

    /**
     * Find inheritance events by inheritance rule ID and created by and event type
     */
    List<InheritanceEvent> findByInheritanceRuleIdAndCreatedByAndEventType(UUID inheritanceRuleId, UUID createdBy, InheritanceEvent.EventType eventType);

    /**
     * Find inheritance events by inheritance rule ID and created by and event type with pagination
     */
    Page<InheritanceEvent> findByInheritanceRuleIdAndCreatedByAndEventType(UUID inheritanceRuleId, UUID createdBy, InheritanceEvent.EventType eventType, Pageable pageable);

    /**
     * Find inheritance events by inheritance rule ID and created by and created date range
     */
    List<InheritanceEvent> findByInheritanceRuleIdAndCreatedByAndCreatedAtBetween(UUID inheritanceRuleId, UUID createdBy, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inheritance events by inheritance rule ID and created by and created date range with pagination
     */
    Page<InheritanceEvent> findByInheritanceRuleIdAndCreatedByAndCreatedAtBetween(UUID inheritanceRuleId, UUID createdBy, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find inheritance events by inheritance rule ID and created by and event type and created date range
     */
    List<InheritanceEvent> findByInheritanceRuleIdAndCreatedByAndEventTypeAndCreatedAtBetween(UUID inheritanceRuleId, UUID createdBy, InheritanceEvent.EventType eventType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find inheritance events by inheritance rule ID and created by and event type and created date range with pagination
     */
    Page<InheritanceEvent> findByInheritanceRuleIdAndCreatedByAndEventTypeAndCreatedAtBetween(UUID inheritanceRuleId, UUID createdBy, InheritanceEvent.EventType eventType, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
