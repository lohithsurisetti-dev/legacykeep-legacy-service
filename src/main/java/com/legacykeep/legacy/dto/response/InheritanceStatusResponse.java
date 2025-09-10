package com.legacykeep.legacy.dto.response;

import com.legacykeep.legacy.entity.InheritanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for inheritance status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InheritanceStatusResponse {

    private UUID id;
    private UUID contentId;
    private UUID recipientId;
    private UUID inheritanceRuleId;
    private InheritanceStatus.Status status;
    private LocalDateTime inheritedAt;
    private LocalDateTime accessedAt;
    private LocalDateTime declinedAt;
    private Long relationshipTypeId;
    private Map<String, Object> relationshipContext;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
