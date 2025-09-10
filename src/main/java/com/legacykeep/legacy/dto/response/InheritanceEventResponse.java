package com.legacykeep.legacy.dto.response;

import com.legacykeep.legacy.entity.InheritanceEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for inheritance events.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InheritanceEventResponse {

    private UUID id;
    private UUID inheritanceRuleId;
    private InheritanceEvent.EventType eventType;
    private Map<String, Object> eventData;
    private LocalDateTime createdAt;
    private UUID createdBy;
}
