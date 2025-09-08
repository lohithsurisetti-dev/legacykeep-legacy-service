package com.legacykeep.legacy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * LegacyRecipient Entity
 * 
 * Represents targeted delivery system for legacy content to specific recipients.
 * Each recipient can have different access levels and personal messages.
 * 
 * Features:
 * - Targeted delivery to specific users, generations, or relationships
 * - Different access levels (read, comment, edit)
 * - Personal messages from content creators
 * - Status tracking (pending, accepted, rejected, expired)
 * - Relationship-based targeting (e.g., "elder son", "both sons")
 */
@Entity
@Table(name = "legacy_recipients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegacyRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @NotNull(message = "Content is required")
    @Column(name = "content_id", nullable = false, columnDefinition = "UUID")
    private UUID contentId;

    @NotNull(message = "Recipient is required")
    @Column(name = "recipient_id", nullable = false, columnDefinition = "UUID")
    private UUID recipientId;

    @NotNull(message = "Recipient type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type", nullable = false, length = 50)
    private RecipientType recipientType;

    @Size(max = 100, message = "Recipient relationship must not exceed 100 characters")
    @Column(name = "recipient_relationship", length = 100)
    private String recipientRelationship;

    @NotNull(message = "Access level is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false, length = 50)
    @Builder.Default
    private AccessLevel accessLevel = AccessLevel.READ;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private RecipientStatus status = RecipientStatus.PENDING;

    @Size(max = 1000, message = "Personal message must not exceed 1000 characters")
    @Column(name = "personal_message", columnDefinition = "TEXT")
    private String personalMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    // ==============================================
    // RELATIONSHIPS
    // ==============================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", insertable = false, updatable = false)
    private LegacyContent content;

    // ==============================================
    // ENUMS
    // ==============================================

    public enum RecipientType {
        SPECIFIC_USER("Targeted to a specific user"),
        GENERATION("Targeted to a specific generation"),
        RELATIONSHIP("Targeted based on relationship");

        private final String description;

        RecipientType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum AccessLevel {
        READ("Can only view the content"),
        COMMENT("Can view and comment on the content"),
        EDIT("Can view, comment, and edit the content");

        private final String description;

        AccessLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum RecipientStatus {
        PENDING("Waiting for recipient to accept"),
        ACCEPTED("Recipient has accepted the content"),
        REJECTED("Recipient has rejected the content"),
        EXPIRED("Invitation has expired");

        private final String description;

        RecipientStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==============================================
    // BUSINESS LOGIC METHODS
    // ==============================================

    /**
     * Check if the recipient has accepted the content
     */
    public boolean isAccepted() {
        return status == RecipientStatus.ACCEPTED;
    }

    /**
     * Check if the recipient has rejected the content
     */
    public boolean isRejected() {
        return status == RecipientStatus.REJECTED;
    }

    /**
     * Check if the invitation is still pending
     */
    public boolean isPending() {
        return status == RecipientStatus.PENDING;
    }

    /**
     * Check if the invitation has expired
     */
    public boolean isExpired() {
        return status == RecipientStatus.EXPIRED;
    }

    /**
     * Check if the recipient can edit the content
     */
    public boolean canEdit() {
        return accessLevel == AccessLevel.EDIT && isAccepted();
    }

    /**
     * Check if the recipient can comment on the content
     */
    public boolean canComment() {
        return (accessLevel == AccessLevel.COMMENT || accessLevel == AccessLevel.EDIT) && isAccepted();
    }

    /**
     * Check if the recipient can view the content
     */
    public boolean canView() {
        return isAccepted();
    }

    /**
     * Get the recipient's display name based on type and relationship
     */
    public String getRecipientDisplayName() {
        if (recipientRelationship != null && !recipientRelationship.isEmpty()) {
            return recipientRelationship;
        }
        
        switch (recipientType) {
            case SPECIFIC_USER:
                return "Specific User";
            case GENERATION:
                return "Generation";
            case RELATIONSHIP:
                return "Relationship";
            default:
                return "Unknown";
        }
    }

    /**
     * Check if this recipient can be deleted
     */
    public boolean canBeDeleted() {
        return status == RecipientStatus.PENDING || status == RecipientStatus.EXPIRED;
    }

    /**
     * Get the status description for UI display
     */
    public String getStatusDescription() {
        return status.getDescription();
    }

    /**
     * Get the access level description for UI display
     */
    public String getAccessLevelDescription() {
        return accessLevel.getDescription();
    }

    // ==============================================
    // VALIDATION METHODS
    // ==============================================

    /**
     * Validate that the recipient type matches the relationship
     */
    public boolean isRecipientTypeValid() {
        if (recipientType == RecipientType.RELATIONSHIP && 
            (recipientRelationship == null || recipientRelationship.trim().isEmpty())) {
            return false; // Relationship type requires a relationship description
        }
        return true;
    }

    /**
     * Validate that the access level is appropriate for the status
     */
    public boolean isAccessLevelValid() {
        // Access level is valid for all statuses
        return accessLevel != null;
    }

    // ==============================================
    // UTILITY METHODS
    // ==============================================

    @PrePersist
    @PreUpdate
    private void validateRecipient() {
        if (!isRecipientTypeValid()) {
            throw new IllegalStateException("Recipient type does not match relationship");
        }
        if (!isAccessLevelValid()) {
            throw new IllegalStateException("Access level is not valid");
        }
    }

    /**
     * Get a summary of the recipient
     */
    public String getRecipientSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Type: ").append(recipientType.getDescription());
        
        if (recipientRelationship != null && !recipientRelationship.isEmpty()) {
            summary.append(", Relationship: ").append(recipientRelationship);
        }
        
        summary.append(", Access: ").append(accessLevel.getDescription());
        summary.append(", Status: ").append(status.getDescription());
        
        return summary.toString();
    }

    @Override
    public String toString() {
        return String.format("LegacyRecipient{id=%s, type=%s, relationship='%s', access=%s, status=%s}", 
                id, recipientType, recipientRelationship, accessLevel, status);
    }
}
