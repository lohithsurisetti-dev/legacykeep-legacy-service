package com.legacykeep.legacy.dto.response;

import com.legacykeep.legacy.entity.LegacyMediaFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Response DTO for legacy media file
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFileResponse {

    private UUID id;
    private UUID contentId;
    private String fileName;
    private String originalFileName;
    private Long fileSize;
    private String mimeType;
    private String s3Url;
    private String thumbnailUrl;
    private LegacyMediaFile.FileType fileType;
    private LegacyMediaFile.ProcessingStatus processingStatus;
    private ZonedDateTime createdAt;

    // Additional computed fields
    private String fileExtension;
    private String fileNameWithoutExtension;
    private String formattedFileSize;
    private Boolean hasThumbnail;
    private Boolean isProcessingCompleted;
    private Boolean isProcessingFailed;
    private Boolean isProcessing;
    private Boolean isPendingProcessing;
    private String mediaFileSummary;
}
