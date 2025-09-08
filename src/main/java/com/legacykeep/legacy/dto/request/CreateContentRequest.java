package com.legacykeep.legacy.dto.request;

import com.legacykeep.legacy.entity.LegacyContent;
import com.legacykeep.legacy.entity.LegacyMediaFile;
import com.legacykeep.legacy.entity.LegacyRecipient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating new legacy content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateContentRequest {

    @NotBlank(message = "Content title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;

    @NotNull(message = "Content type is required")
    private LegacyContent.ContentType contentType;

    @NotNull(message = "Bucket is required")
    private UUID bucketId;

    @NotNull(message = "Creator is required")
    private UUID creatorId;

    @NotNull(message = "Family is required")
    private UUID familyId;

    private Integer generationLevel;

    @Builder.Default
    private LegacyContent.PrivacyLevel privacyLevel = LegacyContent.PrivacyLevel.FAMILY;

    private Boolean isFeatured;

    private Integer sortOrder;

    // Media files to be uploaded
    private List<MediaFileRequest> mediaFiles;

    // Recipients for targeted delivery
    private List<RecipientRequest> recipients;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaFileRequest {
        @NotBlank(message = "File name is required")
        private String fileName;

        @NotBlank(message = "Original file name is required")
        private String originalFileName;

        @NotNull(message = "File size is required")
        private Long fileSize;

        @NotBlank(message = "MIME type is required")
        private String mimeType;

        @NotBlank(message = "S3 URL is required")
        private String s3Url;

        private String thumbnailUrl;

        @NotNull(message = "File type is required")
        private LegacyMediaFile.FileType fileType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipientRequest {
        @NotNull(message = "Recipient is required")
        private UUID recipientId;

        @NotNull(message = "Recipient type is required")
        private LegacyRecipient.RecipientType recipientType;

        @Size(max = 100, message = "Recipient relationship must not exceed 100 characters")
        private String recipientRelationship;

        @Builder.Default
        private LegacyRecipient.AccessLevel accessLevel = LegacyRecipient.AccessLevel.READ;

        @Size(max = 1000, message = "Personal message must not exceed 1000 characters")
        private String personalMessage;
    }
}
