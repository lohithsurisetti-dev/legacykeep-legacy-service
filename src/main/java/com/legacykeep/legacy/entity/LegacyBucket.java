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
 * LegacyBucket Entity
 * 
 * Represents user-created collections/buckets for organizing legacy content.
 * Each bucket belongs to a category and contains multiple legacy content items.
 * 
 * Features:
 * - User-created collections for organizing content
 * - Category-based organization
 * - Privacy levels for access control
 * - Featured buckets for highlighting important collections
 * - Sort ordering for consistent display
 */
@Entity
@Table(name = "legacy_buckets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegacyBucket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @NotBlank(message = "Bucket name is required")
    @Size(max = 255, message = "Bucket name must not exceed 255 characters")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Category is required")
    @Column(name = "category_id", nullable = false, columnDefinition = "UUID")
    private UUID categoryId;

    @NotNull(message = "Creator is required")
    @Column(name = "creator_id", nullable = false, columnDefinition = "UUID")
    private UUID creatorId;

    @NotNull(message = "Family is required")
    @Column(name = "family_id", nullable = false, columnDefinition = "UUID")
    private UUID familyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "bucket_type", nullable = false, length = 50)
    @Builder.Default
    private BucketType bucketType = BucketType.CUSTOM;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy_level", nullable = false, length = 50)
    @Builder.Default
    private PrivacyLevel privacyLevel = PrivacyLevel.FAMILY;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private BucketStatus status = BucketStatus.ACTIVE;

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
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private LegacyCategory category;

    @OneToMany(mappedBy = "bucket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LegacyContent> contents = new ArrayList<>();

    // ==============================================
    // ENUMS
    // ==============================================

    public enum BucketType {
        CUSTOM("User-created custom bucket"),
        SYSTEM("System-generated bucket");

        private final String description;

        BucketType(String description) {
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

    public enum BucketStatus {
        ACTIVE("Active and visible"),
        INACTIVE("Inactive but preserved"),
        ARCHIVED("Archived for long-term storage"),
        DELETED("Marked for deletion");

        private final String description;

        BucketStatus(String description) {
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
     * Get the number of content items in this bucket
     */
    public int getContentCount() {
        return contents != null ? contents.size() : 0;
    }

    /**
     * Check if the bucket is empty
     */
    public boolean isEmpty() {
        return getContentCount() == 0;
    }

    /**
     * Check if the bucket can be deleted
     */
    public boolean canBeDeleted() {
        return status != BucketStatus.DELETED;
    }

    /**
     * Check if the bucket is active
     */
    public boolean isActive() {
        return status == BucketStatus.ACTIVE;
    }

    /**
     * Check if the bucket is deleted
     */
    public boolean isDeleted() {
        return status == BucketStatus.DELETED;
    }

    /**
     * Get the total size of all media files in this bucket
     */
    public long getTotalMediaSize() {
        if (contents == null) {
            return 0L;
        }
        
        return contents.stream()
                .flatMap(content -> content.getMediaFiles().stream())
                .mapToLong(media -> media.getFileSize() != null ? media.getFileSize() : 0L)
                .sum();
    }

    /**
     * Check if user has access to this bucket based on privacy level
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
     * Get the bucket's full path including category
     */
    public String getFullPath() {
        if (category != null) {
            return category.getFullPath() + " > " + name;
        }
        return name;
    }

    // ==============================================
    // VALIDATION METHODS
    // ==============================================

    /**
     * Validate that the bucket name is unique within the category
     */
    public boolean isNameUniqueWithinCategory() {
        // This will be validated at the service level
        return true;
    }

    /**
     * Validate that the privacy level is appropriate for the bucket type
     */
    public boolean isPrivacyLevelValid() {
        if (bucketType == BucketType.SYSTEM && privacyLevel == PrivacyLevel.PRIVATE) {
            return false; // System buckets should not be private
        }
        return true;
    }

    // ==============================================
    // UTILITY METHODS
    // ==============================================

    @PrePersist
    @PreUpdate
    private void validateBucket() {
        if (!isPrivacyLevelValid()) {
            throw new IllegalStateException("Privacy level is not valid for bucket type");
        }
    }

    /**
     * Get a summary of the bucket's content
     */
    public String getContentSummary() {
        if (isEmpty()) {
            return "Empty bucket";
        }
        
        int contentCount = getContentCount();
        long mediaSize = getTotalMediaSize();
        
        if (mediaSize > 0) {
            return String.format("%d items, %s", contentCount, formatFileSize(mediaSize));
        } else {
            return String.format("%d items", contentCount);
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    @Override
    public String toString() {
        return String.format("LegacyBucket{id=%s, name='%s', type=%s, privacy=%s, featured=%s}", 
                id, name, bucketType, privacyLevel, isFeatured);
    }
}
