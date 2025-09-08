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

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * LegacyMediaFile Entity
 * 
 * Represents metadata for media files stored in AWS S3.
 * Each media file is associated with legacy content and contains S3 URLs and processing information.
 * 
 * Features:
 * - S3 integration for cloud storage
 * - Multiple file types (audio, video, image, document)
 * - Thumbnail generation support
 * - File processing status tracking
 * - File size and metadata storage
 */
@Entity
@Table(name = "legacy_media_files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegacyMediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @NotNull(message = "Content is required")
    @Column(name = "content_id", nullable = false, columnDefinition = "UUID")
    private UUID contentId;

    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name must not exceed 255 characters")
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @NotBlank(message = "Original file name is required")
    @Size(max = 255, message = "Original file name must not exceed 255 characters")
    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @NotNull(message = "File size is required")
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @NotBlank(message = "MIME type is required")
    @Size(max = 100, message = "MIME type must not exceed 100 characters")
    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @NotBlank(message = "S3 URL is required")
    @Column(name = "s3_url", nullable = false, columnDefinition = "TEXT")
    private String s3Url;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @NotNull(message = "File type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false, length = 50)
    private FileType fileType;

    @NotNull(message = "Processing status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false, length = 50)
    @Builder.Default
    private ProcessingStatus processingStatus = ProcessingStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    // ==============================================
    // RELATIONSHIPS
    // ==============================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", insertable = false, updatable = false)
    private LegacyContent content;

    // ==============================================
    // ENUMS
    // ==============================================

    public enum FileType {
        AUDIO("Audio files"),
        VIDEO("Video files"),
        IMAGE("Image files"),
        DOCUMENT("Document files");

        private final String description;

        FileType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ProcessingStatus {
        PENDING("Waiting to be processed"),
        PROCESSING("Currently being processed"),
        COMPLETED("Processing completed successfully"),
        FAILED("Processing failed");

        private final String description;

        ProcessingStatus(String description) {
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
     * Check if the file is an image
     */
    public boolean isImage() {
        return fileType == FileType.IMAGE;
    }

    /**
     * Check if the file is a video
     */
    public boolean isVideo() {
        return fileType == FileType.VIDEO;
    }

    /**
     * Check if the file is audio
     */
    public boolean isAudio() {
        return fileType == FileType.AUDIO;
    }

    /**
     * Check if the file is a document
     */
    public boolean isDocument() {
        return fileType == FileType.DOCUMENT;
    }

    /**
     * Check if the file processing is completed
     */
    public boolean isProcessingCompleted() {
        return processingStatus == ProcessingStatus.COMPLETED;
    }

    /**
     * Check if the file processing failed
     */
    public boolean isProcessingFailed() {
        return processingStatus == ProcessingStatus.FAILED;
    }

    /**
     * Check if the file is currently being processed
     */
    public boolean isProcessing() {
        return processingStatus == ProcessingStatus.PROCESSING;
    }

    /**
     * Check if the file is pending processing
     */
    public boolean isPendingProcessing() {
        return processingStatus == ProcessingStatus.PENDING;
    }

    /**
     * Check if the file has a thumbnail
     */
    public boolean hasThumbnail() {
        return thumbnailUrl != null && !thumbnailUrl.trim().isEmpty();
    }

    /**
     * Get the file extension from the original file name
     */
    public String getFileExtension() {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < originalFileName.length() - 1) {
            return originalFileName.substring(lastDotIndex + 1).toLowerCase();
        }
        
        return "";
    }

    /**
     * Get the file name without extension
     */
    public String getFileNameWithoutExtension() {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return originalFileName.substring(0, lastDotIndex);
        }
        
        return originalFileName;
    }

    /**
     * Get the formatted file size
     */
    public String getFormattedFileSize() {
        if (fileSize == null) {
            return "Unknown size";
        }
        
        return formatFileSize(fileSize);
    }

    /**
     * Check if the file type matches the MIME type
     */
    public boolean isFileTypeValid() {
        if (mimeType == null || fileType == null) {
            return false;
        }
        
        String mime = mimeType.toLowerCase();
        
        switch (fileType) {
            case AUDIO:
                return mime.startsWith("audio/");
            case VIDEO:
                return mime.startsWith("video/");
            case IMAGE:
                return mime.startsWith("image/");
            case DOCUMENT:
                return mime.startsWith("application/") || mime.startsWith("text/");
            default:
                return false;
        }
    }

    // ==============================================
    // VALIDATION METHODS
    // ==============================================

    /**
     * Validate that the file size is reasonable
     */
    public boolean isFileSizeValid() {
        if (fileSize == null || fileSize <= 0) {
            return false;
        }
        
        // Maximum file size: 100MB
        long maxSize = 100 * 1024 * 1024;
        return fileSize <= maxSize;
    }

    /**
     * Validate that the S3 URL is properly formatted
     */
    public boolean isS3UrlValid() {
        if (s3Url == null || s3Url.trim().isEmpty()) {
            return false;
        }
        
        // Basic S3 URL validation
        return s3Url.startsWith("https://") && s3Url.contains("amazonaws.com");
    }

    // ==============================================
    // UTILITY METHODS
    // ==============================================

    @PrePersist
    @PreUpdate
    private void validateMediaFile() {
        if (!isFileTypeValid()) {
            throw new IllegalStateException("File type does not match MIME type");
        }
        if (!isFileSizeValid()) {
            throw new IllegalStateException("File size is not valid");
        }
        if (!isS3UrlValid()) {
            throw new IllegalStateException("S3 URL is not valid");
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    /**
     * Get a summary of the media file
     */
    public String getMediaFileSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Type: ").append(fileType.getDescription());
        summary.append(", Size: ").append(getFormattedFileSize());
        summary.append(", Status: ").append(processingStatus.getDescription());
        
        if (hasThumbnail()) {
            summary.append(", Has Thumbnail");
        }
        
        return summary.toString();
    }

    @Override
    public String toString() {
        return String.format("LegacyMediaFile{id=%s, name='%s', type=%s, size=%s, status=%s}", 
                id, fileName, fileType, getFormattedFileSize(), processingStatus);
    }
}
