package com.legacykeep.legacy.dto.response;

import com.legacykeep.legacy.entity.LegacyRecipient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Response DTO for legacy recipient
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipientResponse {

    private UUID id;
    private UUID contentId;
    private UUID recipientId;
    private LegacyRecipient.RecipientType recipientType;
    private String recipientRelationship;
    private LegacyRecipient.AccessLevel accessLevel;
    private LegacyRecipient.RecipientStatus status;
    private String personalMessage;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    // Additional computed fields
    private String recipientDisplayName;
    private String statusDescription;
    private String accessLevelDescription;
    private Boolean canBeDeleted;
    private Boolean isAccepted;
    private Boolean isRejected;
    private Boolean isPending;
    private Boolean isExpired;
    private Boolean canEdit;
    private Boolean canComment;
    private Boolean canView;
    private String recipientSummary;
}
