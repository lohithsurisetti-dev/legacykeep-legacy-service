package com.legacykeep.legacy.dto.response;

import com.legacykeep.legacy.enums.InheritanceStatus;
import com.legacykeep.legacy.enums.InheritanceTrigger;
import com.legacykeep.legacy.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for inheritance rules.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InheritanceRuleResponse {

    private UUID id;
    private UUID contentId;
    private UUID creatorId;
    private TargetType targetType;
    private String targetValue;
    private Map<String, Object> targetMetadata;
    private InheritanceTrigger inheritanceTrigger;
    private Map<String, Object> triggerMetadata;
    private InheritanceStatus status;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}
