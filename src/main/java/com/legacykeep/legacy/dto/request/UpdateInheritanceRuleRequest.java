package com.legacykeep.legacy.dto.request;

import com.legacykeep.legacy.enums.InheritanceStatus;
import com.legacykeep.legacy.enums.InheritanceTrigger;
import com.legacykeep.legacy.enums.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for updating inheritance rules.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInheritanceRuleRequest {

    private TargetType targetType;

    @NotBlank(message = "Target value cannot be blank")
    private String targetValue;

    private Map<String, Object> targetMetadata;

    private InheritanceTrigger inheritanceTrigger;

    private Map<String, Object> triggerMetadata;

    private InheritanceStatus status;

    @PositiveOrZero(message = "Priority must be non-negative")
    private Integer priority;
}
