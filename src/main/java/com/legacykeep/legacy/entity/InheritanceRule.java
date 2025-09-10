package com.legacykeep.legacy.entity;

import com.legacykeep.legacy.enums.TargetType;
import com.legacykeep.legacy.enums.InheritanceTrigger;
import com.legacykeep.legacy.enums.InheritanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing an inheritance rule for legacy content.
 * Defines how content should be inherited by specific relationship types or contexts.
 */
@Entity
@Table(name = "inheritance_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InheritanceRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "content_id", nullable = false)
    private UUID contentId;

    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;

    // Targeting Configuration
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 50)
    private TargetType targetType;

    @Column(name = "target_value", nullable = false, length = 255)
    private String targetValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "target_metadata", columnDefinition = "jsonb")
    private String targetMetadata;

    // Inheritance Configuration
    @Enumerated(EnumType.STRING)
    @Column(name = "inheritance_trigger", nullable = false, length = 50)
    private InheritanceTrigger inheritanceTrigger;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trigger_metadata", columnDefinition = "jsonb")
    private String triggerMetadata;

    // Status and Lifecycle
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private InheritanceStatus status = InheritanceStatus.ACTIVE;

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 0;

    // Audit Fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "updated_by", nullable = false)
    private UUID updatedBy;

    // Relationships
    @OneToMany(mappedBy = "inheritanceRule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<com.legacykeep.legacy.entity.InheritanceStatus> inheritanceStatuses;

    @OneToMany(mappedBy = "inheritanceRule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<InheritanceEvent> inheritanceEvents;

    /**
     * Check if this rule is active
     */
    public boolean isActive() {
        return status == InheritanceStatus.ACTIVE;
    }

    /**
     * Check if this rule is paused
     */
    public boolean isPaused() {
        return status == InheritanceStatus.PAUSED;
    }

    /**
     * Check if this rule is completed
     */
    public boolean isCompleted() {
        return status == InheritanceStatus.COMPLETED;
    }

    /**
     * Check if this rule is cancelled
     */
    public boolean isCancelled() {
        return status == InheritanceStatus.CANCELLED;
    }

    /**
     * Check if this rule has immediate inheritance
     */
    public boolean isImmediateInheritance() {
        return inheritanceTrigger == InheritanceTrigger.IMMEDIATE;
    }

    /**
     * Check if this rule has event-based inheritance
     */
    public boolean isEventBasedInheritance() {
        return inheritanceTrigger == InheritanceTrigger.EVENT_BASED;
    }

    /**
     * Check if this rule has time-based inheritance
     */
    public boolean isTimeBasedInheritance() {
        return inheritanceTrigger == InheritanceTrigger.TIME_BASED;
    }

    /**
     * Check if this rule has manual inheritance
     */
    public boolean isManualInheritance() {
        return inheritanceTrigger == InheritanceTrigger.MANUAL;
    }

    /**
     * Check if this rule targets a specific relationship type
     */
    public boolean targetsRelationshipType() {
        return targetType == TargetType.RELATIONSHIP_TYPE;
    }

    /**
     * Check if this rule targets by generation
     */
    public boolean targetsGeneration() {
        return targetType == TargetType.GENERATION;
    }

    /**
     * Check if this rule targets by context
     */
    public boolean targetsContext() {
        return targetType == TargetType.CONTEXT;
    }

    /**
     * Check if this rule has custom targeting
     */
    public boolean hasCustomTargeting() {
        return targetType == TargetType.CUSTOM;
    }
}
