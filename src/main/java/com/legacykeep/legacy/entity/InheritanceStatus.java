package com.legacykeep.legacy.entity;

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
import java.util.UUID;

/**
 * Entity representing the inheritance status for a specific recipient of legacy content.
 * Tracks the progress of content inheritance from creation to access.
 */
@Entity
@Table(name = "inheritance_status", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"content_id", "recipient_id", "inheritance_rule_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InheritanceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "content_id", nullable = false)
    private UUID contentId;

    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    @Column(name = "inheritance_rule_id", nullable = false)
    private UUID inheritanceRuleId;

    // Status Tracking
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.PENDING;

    @Column(name = "inherited_at")
    private LocalDateTime inheritedAt;

    @Column(name = "accessed_at")
    private LocalDateTime accessedAt;

    @Column(name = "declined_at")
    private LocalDateTime declinedAt;

    // Relationship Context
    @Column(name = "relationship_type_id")
    private Long relationshipTypeId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "relationship_context", columnDefinition = "jsonb")
    private String relationshipContext;

    // Metadata
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    // Audit Fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inheritance_rule_id", insertable = false, updatable = false)
    private InheritanceRule inheritanceRule;

    /**
     * Check if the inheritance is pending
     */
    public boolean isPending() {
        return status == Status.PENDING;
    }

    /**
     * Check if the inheritance is completed
     */
    public boolean isInherited() {
        return status == Status.INHERITED;
    }

    /**
     * Check if the content has been accessed
     */
    public boolean isAccessed() {
        return status == Status.ACCESSED;
    }

    /**
     * Check if the inheritance was declined
     */
    public boolean isDeclined() {
        return status == Status.DECLINED;
    }

    /**
     * Mark as inherited
     */
    public void markAsInherited() {
        this.status = Status.INHERITED;
        this.inheritedAt = LocalDateTime.now();
    }

    /**
     * Mark as accessed
     */
    public void markAsAccessed() {
        this.status = Status.ACCESSED;
        this.accessedAt = LocalDateTime.now();
    }

    /**
     * Mark as declined
     */
    public void markAsDeclined() {
        this.status = Status.DECLINED;
        this.declinedAt = LocalDateTime.now();
    }

    /**
     * Get the time since inheritance
     */
    public long getTimeSinceInheritance() {
        if (inheritedAt == null) {
            return 0;
        }
        return java.time.Duration.between(inheritedAt, LocalDateTime.now()).toMinutes();
    }

    /**
     * Get the time since access
     */
    public long getTimeSinceAccess() {
        if (accessedAt == null) {
            return 0;
        }
        return java.time.Duration.between(accessedAt, LocalDateTime.now()).toMinutes();
    }

    /**
     * Enum for inheritance status
     */
    public enum Status {
        PENDING("Pending", "Inheritance is pending"),
        INHERITED("Inherited", "Content has been inherited"),
        ACCESSED("Accessed", "Content has been accessed by recipient"),
        DECLINED("Declined", "Inheritance was declined by recipient");

        private final String displayName;
        private final String description;

        Status(String displayName, String description) {
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
