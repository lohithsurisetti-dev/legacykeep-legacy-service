package com.legacykeep.legacy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an inheritance event for audit trail purposes.
 * Tracks all inheritance-related activities and state changes.
 */
@Entity
@Table(name = "inheritance_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InheritanceEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "inheritance_rule_id", nullable = false)
    private UUID inheritanceRuleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private EventType eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_data", nullable = false, columnDefinition = "jsonb")
    private String eventData;

    // Audit Fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inheritance_rule_id", insertable = false, updatable = false)
    private InheritanceRule inheritanceRule;

    /**
     * Check if this is a rule-related event
     */
    public boolean isRuleEvent() {
        return eventType == EventType.RULE_CREATED ||
               eventType == EventType.RULE_UPDATED ||
               eventType == EventType.RULE_DELETED ||
               eventType == EventType.RULE_ACTIVATED ||
               eventType == EventType.RULE_PAUSED;
    }

    /**
     * Check if this is a relationship-related event
     */
    public boolean isRelationshipEvent() {
        return eventType == EventType.RELATIONSHIP_ADDED ||
               eventType == EventType.RELATIONSHIP_REMOVED ||
               eventType == EventType.RELATIONSHIP_UPDATED;
    }

    /**
     * Check if this is an inheritance-related event
     */
    public boolean isInheritanceEvent() {
        return eventType == EventType.INHERITANCE_TRIGGERED ||
               eventType == EventType.INHERITANCE_COMPLETED ||
               eventType == EventType.INHERITANCE_FAILED;
    }

    /**
     * Check if this is a content-related event
     */
    public boolean isContentEvent() {
        return eventType == EventType.CONTENT_ACCESSED ||
               eventType == EventType.CONTENT_DECLINED ||
               eventType == EventType.CONTENT_SHARED;
    }

    /**
     * Enum for inheritance event types
     */
    public enum EventType {
        // Rule Events
        RULE_CREATED("Rule Created", "Inheritance rule was created"),
        RULE_UPDATED("Rule Updated", "Inheritance rule was updated"),
        RULE_DELETED("Rule Deleted", "Inheritance rule was deleted"),
        RULE_ACTIVATED("Rule Activated", "Inheritance rule was activated"),
        RULE_PAUSED("Rule Paused", "Inheritance rule was paused"),

        // Relationship Events
        RELATIONSHIP_ADDED("Relationship Added", "New relationship was added"),
        RELATIONSHIP_REMOVED("Relationship Removed", "Relationship was removed"),
        RELATIONSHIP_UPDATED("Relationship Updated", "Relationship was updated"),

        // Inheritance Events
        INHERITANCE_TRIGGERED("Inheritance Triggered", "Inheritance process was triggered"),
        INHERITANCE_COMPLETED("Inheritance Completed", "Inheritance process was completed"),
        INHERITANCE_FAILED("Inheritance Failed", "Inheritance process failed"),

        // Content Events
        CONTENT_ACCESSED("Content Accessed", "Inherited content was accessed"),
        CONTENT_DECLINED("Content Declined", "Inherited content was declined"),
        CONTENT_SHARED("Content Shared", "Inherited content was shared");

        private final String displayName;
        private final String description;

        EventType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }
}
