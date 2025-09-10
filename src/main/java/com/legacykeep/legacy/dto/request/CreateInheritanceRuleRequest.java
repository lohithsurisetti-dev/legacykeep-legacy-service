package com.legacykeep.legacy.dto.request;

import com.legacykeep.legacy.enums.InheritanceTrigger;
import com.legacykeep.legacy.enums.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for creating inheritance rules.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInheritanceRuleRequest {

    @NotNull(message = "Content ID is required")
    private UUID contentId;

    @NotNull(message = "Target type is required")
    private TargetType targetType;

    @NotBlank(message = "Target value is required")
    private String targetValue;

    private Map<String, Object> targetMetadata;

    @NotNull(message = "Inheritance trigger is required")
    private InheritanceTrigger inheritanceTrigger;

    private Map<String, Object> triggerMetadata;

    @PositiveOrZero(message = "Priority must be non-negative")
    private Integer priority;
}
