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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * LegacyContent Entity
 * 
 * Represents individual legacy content items within buckets.
 * Each content item can contain text, media files, and be targeted to specific recipients.
 * 
 * Features:
 * - Multiple content types (text, audio, video, image, document)
 * - Bucket-based organization
 * - Targeted delivery to specific recipients
 * - Privacy levels for access control
 * - Featured content for highlighting important items
 * - Sort ordering for consistent display
 */
@Entity
@Table(name = "legacy_content")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegacyContent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @NotBlank(message = "Content title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @NotNull(message = "Content type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 50)
    private ContentType contentType;

    @NotNull(message = "Bucket is required")
    @Column(name = "bucket_id", nullable = false, columnDefinition = "UUID")
    private UUID bucketId;

    @NotNull(message = "Creator is required")
    @Column(name = "creator_id", nullable = false, columnDefinition = "UUID")
    private UUID creatorId;

    @NotNull(message = "Family is required")
    @Column(name = "family_id", nullable = false, columnDefinition = "UUID")
    private UUID familyId;

    @Column(name = "generation_level")
    private Integer generationLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy_level", nullable = false, length = 50)
    @Builder.Default
    private PrivacyLevel privacyLevel = PrivacyLevel.FAMILY;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private ContentStatus status = ContentStatus.ACTIVE;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

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
    @JoinColumn(name = "bucket_id", insertable = false, updatable = false)
    private LegacyBucket bucket;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LegacyRecipient> recipients = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LegacyMediaFile> mediaFiles = new ArrayList<>();

    // ==============================================
    // ENUMS
    // ==============================================

    public enum ContentType {
        TEXT("Text-based content"),
        AUDIO("Audio recordings"),
        VIDEO("Video content"),
        IMAGE("Image content"),
        DOCUMENT("Document files");

        private final String description;

        ContentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum PrivacyLevel {
        PRIVATE("Only creator can access"),
        FAMILY("Family members can access"),
        EXTENDED_FAMILY("Extended family can access"),
        PUBLIC("Everyone can access");

        private final String description;

        PrivacyLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ContentStatus {
        ACTIVE("Active and visible"),
        INACTIVE("Inactive but preserved"),
        ARCHIVED("Archived for long-term storage"),
        DELETED("Marked for deletion");

        private final String description;

        ContentStatus(String description) {
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
     * Get the number of recipients for this content
     */
    public int getRecipientCount() {
        return recipients != null ? recipients.size() : 0;
    }

    /**
     * Get the number of media files for this content
     */
    public int getMediaFileCount() {
        return mediaFiles != null ? mediaFiles.size() : 0;
    }

    /**
     * Check if this content has media files
     */
    public boolean hasMediaFiles() {
        return getMediaFileCount() > 0;
    }

    /**
     * Check if this content has recipients
     */
    public boolean hasRecipients() {
        return getRecipientCount() > 0;
    }

    /**
     * Get the total size of all media files
     */
    public long getTotalMediaSize() {
        if (mediaFiles == null) {
            return 0L;
        }
        
        return mediaFiles.stream()
                .mapToLong(media -> media.getFileSize() != null ? media.getFileSize() : 0L)
                .sum();
    }

    /**
     * Check if user has access to this content based on privacy level
     */
    public boolean hasAccess(UUID userId, boolean isFamilyMember, boolean isExtendedFamilyMember) {
        switch (privacyLevel) {
            case PRIVATE:
                return creatorId.equals(userId);
            case FAMILY:
                return creatorId.equals(userId) || isFamilyMember;
            case EXTENDED_FAMILY:
                return creatorId.equals(userId) || isFamilyMember || isExtendedFamilyMember;
            case PUBLIC:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if this content can be deleted
     */
    public boolean canBeDeleted() {
        return status != ContentStatus.DELETED;
    }

    /**
     * Check if this content is active
     */
    public boolean isActive() {
        return status == ContentStatus.ACTIVE;
    }

    /**
     * Get the content's full path including bucket and category
     */
    public String getFullPath() {
        if (bucket != null) {
            return bucket.getFullPath() + " > " + title;
        }
        return title;
    }

    /**
     * Get a preview of the content (first 100 characters)
     */
    public String getContentPreview() {
        if (content == null || content.isEmpty()) {
            return "No content preview available";
        }
        
        if (content.length() <= 100) {
            return content;
        }
        
        return content.substring(0, 100) + "...";
    }

    // ==============================================
    // VALIDATION METHODS
    // ==============================================

    /**
     * Validate that the content type matches the actual content
     */
    public boolean isContentTypeValid() {
        if (contentType == null) {
            return false;
        }
        
        switch (contentType) {
            case TEXT:
                return content != null && !content.trim().isEmpty();
            case AUDIO:
            case VIDEO:
            case IMAGE:
            case DOCUMENT:
                return hasMediaFiles();
            default:
                return false;
        }
    }

    /**
     * Validate that the privacy level is appropriate for the content type
     */
    public boolean isPrivacyLevelValid() {
        // All privacy levels are valid for all content types
        return privacyLevel != null;
    }

    // ==============================================
    // UTILITY METHODS
    // ==============================================

    @PrePersist
    @PreUpdate
    private void validateContent() {
        if (!isContentTypeValid()) {
            throw new IllegalStateException("Content type does not match actual content");
        }
        if (!isPrivacyLevelValid()) {
            throw new IllegalStateException("Privacy level is not valid");
        }
    }

    /**
     * Get a summary of the content
     */
    public String getContentSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Type: ").append(contentType.getDescription());
        
        if (hasMediaFiles()) {
            summary.append(", Media: ").append(getMediaFileCount()).append(" files");
            long totalSize = getTotalMediaSize();
            if (totalSize > 0) {
                summary.append(" (").append(formatFileSize(totalSize)).append(")");
            }
        }
        
        if (hasRecipients()) {
            summary.append(", Recipients: ").append(getRecipientCount());
        }
        
        return summary.toString();
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    @Override
    public String toString() {
        return String.format("LegacyContent{id=%s, title='%s', type=%s, status=%s, featured=%s}", 
                id, title, contentType, status, isFeatured);
    }
}
