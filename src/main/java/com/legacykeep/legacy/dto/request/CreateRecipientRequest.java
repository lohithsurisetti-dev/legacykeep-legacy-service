package com.legacykeep.legacy.dto.request;

import com.legacykeep.legacy.entity.LegacyRecipient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating/updating legacy recipients
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecipientRequest {

    @NotNull(message = "Content ID is required")
    private UUID contentId;

    @NotNull(message = "Recipient ID is required")
    private UUID recipientId;

    @NotNull(message = "Recipient type is required")
    private LegacyRecipient.RecipientType recipientType;

    @NotBlank(message = "Recipient relationship is required")
    @Size(max = 100, message = "Recipient relationship must not exceed 100 characters")
    private String recipientRelationship;

    @NotNull(message = "Access level is required")
    private LegacyRecipient.AccessLevel accessLevel;

    @Size(max = 1000, message = "Personal message must not exceed 1000 characters")
    private String personalMessage;

    // Optional: Status can be set during creation
    private LegacyRecipient.RecipientStatus status;
}
