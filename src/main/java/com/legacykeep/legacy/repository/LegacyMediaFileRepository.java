package com.legacykeep.legacy.repository;

import com.legacykeep.legacy.entity.LegacyMediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for LegacyMediaFile entity
 * Handles database operations for media file metadata
 */
@Repository
public interface LegacyMediaFileRepository extends JpaRepository<LegacyMediaFile, UUID> {

    /**
     * Find media files by content
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.contentId = :contentId ORDER BY m.createdAt")
    List<LegacyMediaFile> findByContentId(@Param("contentId") UUID contentId);

    /**
     * Find media files by file type
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.fileType = :fileType ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findByFileType(@Param("fileType") LegacyMediaFile.FileType fileType);

    /**
     * Find media files by processing status
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.processingStatus = :status ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findByProcessingStatus(@Param("status") LegacyMediaFile.ProcessingStatus status);

    /**
     * Find media files by MIME type
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.mimeType = :mimeType ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findByMimeType(@Param("mimeType") String mimeType);

    /**
     * Find media files by MIME type containing
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.mimeType LIKE CONCAT('%', :mimeType, '%') ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findByMimeTypeContaining(@Param("mimeType") String mimeType);

    /**
     * Find media files by file name containing (case insensitive)
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE LOWER(m.fileName) LIKE LOWER(CONCAT('%', :fileName, '%')) ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findByFileNameContainingIgnoreCase(@Param("fileName") String fileName);

    /**
     * Find media files by original file name containing (case insensitive)
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE LOWER(m.originalFileName) LIKE LOWER(CONCAT('%', :originalFileName, '%')) ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findByOriginalFileNameContainingIgnoreCase(@Param("originalFileName") String originalFileName);

    /**
     * Find media files by S3 URL
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.s3Url = :s3Url")
    Optional<LegacyMediaFile> findByS3Url(@Param("s3Url") String s3Url);

    /**
     * Find media files with thumbnails
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.thumbnailUrl IS NOT NULL AND m.thumbnailUrl != '' ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findMediaFilesWithThumbnails();

    /**
     * Find media files without thumbnails
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.thumbnailUrl IS NULL OR m.thumbnailUrl = '' ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findMediaFilesWithoutThumbnails();

    /**
     * Find pending media files (for processing)
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.processingStatus = 'PENDING' ORDER BY m.createdAt")
    List<LegacyMediaFile> findPendingMediaFiles();

    /**
     * Find processing media files
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.processingStatus = 'PROCESSING' ORDER BY m.createdAt")
    List<LegacyMediaFile> findProcessingMediaFiles();

    /**
     * Find completed media files
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.processingStatus = 'COMPLETED' ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findCompletedMediaFiles();

    /**
     * Find failed media files
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.processingStatus = 'FAILED' ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findFailedMediaFiles();

    /**
     * Find media files by content and file type
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.contentId = :contentId AND m.fileType = :fileType ORDER BY m.createdAt")
    List<LegacyMediaFile> findByContentIdAndFileType(@Param("contentId") UUID contentId, @Param("fileType") LegacyMediaFile.FileType fileType);

    /**
     * Find media files by content and processing status
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.contentId = :contentId AND m.processingStatus = :status ORDER BY m.createdAt")
    List<LegacyMediaFile> findByContentIdAndProcessingStatus(@Param("contentId") UUID contentId, @Param("status") LegacyMediaFile.ProcessingStatus status);

    /**
     * Count media files by content
     */
    @Query("SELECT COUNT(m) FROM LegacyMediaFile m WHERE m.contentId = :contentId")
    long countByContentId(@Param("contentId") UUID contentId);

    /**
     * Count media files by file type
     */
    @Query("SELECT COUNT(m) FROM LegacyMediaFile m WHERE m.fileType = :fileType")
    long countByFileType(@Param("fileType") LegacyMediaFile.FileType fileType);

    /**
     * Count media files by processing status
     */
    @Query("SELECT COUNT(m) FROM LegacyMediaFile m WHERE m.processingStatus = :status")
    long countByProcessingStatus(@Param("status") LegacyMediaFile.ProcessingStatus status);

    /**
     * Count pending media files
     */
    @Query("SELECT COUNT(m) FROM LegacyMediaFile m WHERE m.processingStatus = 'PENDING'")
    long countPendingMediaFiles();

    /**
     * Count processing media files
     */
    @Query("SELECT COUNT(m) FROM LegacyMediaFile m WHERE m.processingStatus = 'PROCESSING'")
    long countProcessingMediaFiles();

    /**
     * Count completed media files
     */
    @Query("SELECT COUNT(m) FROM LegacyMediaFile m WHERE m.processingStatus = 'COMPLETED'")
    long countCompletedMediaFiles();

    /**
     * Count failed media files
     */
    @Query("SELECT COUNT(m) FROM LegacyMediaFile m WHERE m.processingStatus = 'FAILED'")
    long countFailedMediaFiles();

    /**
     * Find media files by file size range
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.fileSize BETWEEN :minSize AND :maxSize ORDER BY m.fileSize DESC")
    List<LegacyMediaFile> findByFileSizeBetween(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize);

    /**
     * Find large media files (above threshold)
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE m.fileSize > :threshold ORDER BY m.fileSize DESC")
    List<LegacyMediaFile> findLargeMediaFiles(@Param("threshold") Long threshold);

    /**
     * Find media files by content creator (through content relationship)
     */
    @Query("SELECT m FROM LegacyMediaFile m INNER JOIN LegacyContent c ON m.contentId = c.id WHERE c.creatorId = :creatorId ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findMediaFilesByContentCreator(@Param("creatorId") UUID creatorId);

    /**
     * Find media files by content family (through content relationship)
     */
    @Query("SELECT m FROM LegacyMediaFile m INNER JOIN LegacyContent c ON m.contentId = c.id WHERE c.familyId = :familyId ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findMediaFilesByContentFamily(@Param("familyId") UUID familyId);

    /**
     * Find media files by content bucket (through content relationship)
     */
    @Query("SELECT m FROM LegacyMediaFile m INNER JOIN LegacyContent c ON m.contentId = c.id WHERE c.bucketId = :bucketId ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findMediaFilesByContentBucket(@Param("bucketId") UUID bucketId);

    /**
     * Find media files by content creator and file type
     */
    @Query("SELECT m FROM LegacyMediaFile m INNER JOIN LegacyContent c ON m.contentId = c.id WHERE c.creatorId = :creatorId AND m.fileType = :fileType ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findMediaFilesByContentCreatorAndFileType(@Param("creatorId") UUID creatorId, @Param("fileType") LegacyMediaFile.FileType fileType);

    /**
     * Find media files by content creator and processing status
     */
    @Query("SELECT m FROM LegacyMediaFile m INNER JOIN LegacyContent c ON m.contentId = c.id WHERE c.creatorId = :creatorId AND m.processingStatus = :status ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findMediaFilesByContentCreatorAndProcessingStatus(@Param("creatorId") UUID creatorId, @Param("status") LegacyMediaFile.ProcessingStatus status);

    /**
     * Get total file size by content
     */
    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM LegacyMediaFile m WHERE m.contentId = :contentId")
    Long getTotalFileSizeByContentId(@Param("contentId") UUID contentId);

    /**
     * Get total file size by content creator
     */
    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM LegacyMediaFile m INNER JOIN LegacyContent c ON m.contentId = c.id WHERE c.creatorId = :creatorId")
    Long getTotalFileSizeByContentCreator(@Param("creatorId") UUID creatorId);

    /**
     * Get total file size by content family
     */
    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM LegacyMediaFile m INNER JOIN LegacyContent c ON m.contentId = c.id WHERE c.familyId = :familyId")
    Long getTotalFileSizeByContentFamily(@Param("familyId") UUID familyId);

    /**
     * Get total file size by content bucket
     */
    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM LegacyMediaFile m INNER JOIN LegacyContent c ON m.contentId = c.id WHERE c.bucketId = :bucketId")
    Long getTotalFileSizeByContentBucket(@Param("bucketId") UUID bucketId);

    /**
     * Check if media file exists by S3 URL
     */
    @Query("SELECT COUNT(m) > 0 FROM LegacyMediaFile m WHERE m.s3Url = :s3Url")
    boolean existsByS3Url(@Param("s3Url") String s3Url);

    /**
     * Find media files by file extension
     */
    @Query("SELECT m FROM LegacyMediaFile m WHERE LOWER(m.originalFileName) LIKE LOWER(CONCAT('%.', :extension)) ORDER BY m.createdAt DESC")
    List<LegacyMediaFile> findByFileExtension(@Param("extension") String extension);
}
