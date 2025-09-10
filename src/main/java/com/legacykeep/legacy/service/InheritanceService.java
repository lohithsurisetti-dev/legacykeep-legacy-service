package com.legacykeep.legacy.service;

import com.legacykeep.legacy.dto.request.CreateInheritanceRuleRequest;
import com.legacykeep.legacy.dto.request.UpdateInheritanceRuleRequest;
import com.legacykeep.legacy.dto.response.InheritanceEventResponse;
import com.legacykeep.legacy.dto.response.InheritanceRuleResponse;
import com.legacykeep.legacy.dto.response.InheritanceStatusResponse;
import com.legacykeep.legacy.enums.InheritanceStatus;
import com.legacykeep.legacy.enums.InheritanceTrigger;
import com.legacykeep.legacy.enums.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for inheritance management.
 * Provides business logic for inheritance rules, status tracking, and event processing.
 */
public interface InheritanceService {

    // Inheritance Rule Management
    /**
     * Create a new inheritance rule
     */
    InheritanceRuleResponse createInheritanceRule(CreateInheritanceRuleRequest request, UUID creatorId);

    /**
     * Update an existing inheritance rule
     */
    InheritanceRuleResponse updateInheritanceRule(UUID ruleId, UpdateInheritanceRuleRequest request, UUID updatedBy);

    /**
     * Get inheritance rule by ID
     */
    InheritanceRuleResponse getInheritanceRuleById(UUID ruleId);

    /**
     * Get all inheritance rules for a content
     */
    List<InheritanceRuleResponse> getInheritanceRulesByContentId(UUID contentId);

    /**
     * Get all inheritance rules for a content with pagination
     */
    Page<InheritanceRuleResponse> getInheritanceRulesByContentId(UUID contentId, Pageable pageable);

    /**
     * Get all inheritance rules for a creator
     */
    List<InheritanceRuleResponse> getInheritanceRulesByCreatorId(UUID creatorId);

    /**
     * Get all inheritance rules for a creator with pagination
     */
    Page<InheritanceRuleResponse> getInheritanceRulesByCreatorId(UUID creatorId, Pageable pageable);

    /**
     * Get inheritance rules by status
     */
    List<InheritanceRuleResponse> getInheritanceRulesByStatus(InheritanceStatus status);

    /**
     * Get inheritance rules by status with pagination
     */
    Page<InheritanceRuleResponse> getInheritanceRulesByStatus(InheritanceStatus status, Pageable pageable);

    /**
     * Get inheritance rules by target type and value
     */
    List<InheritanceRuleResponse> getInheritanceRulesByTarget(TargetType targetType, String targetValue);

    /**
     * Get inheritance rules by inheritance trigger
     */
    List<InheritanceRuleResponse> getInheritanceRulesByTrigger(InheritanceTrigger trigger);

    /**
     * Delete an inheritance rule
     */
    void deleteInheritanceRule(UUID ruleId, UUID deletedBy);

    /**
     * Activate an inheritance rule
     */
    InheritanceRuleResponse activateInheritanceRule(UUID ruleId, UUID activatedBy);

    /**
     * Pause an inheritance rule
     */
    InheritanceRuleResponse pauseInheritanceRule(UUID ruleId, UUID pausedBy);

    /**
     * Complete an inheritance rule
     */
    InheritanceRuleResponse completeInheritanceRule(UUID ruleId, UUID completedBy);

    /**
     * Cancel an inheritance rule
     */
    InheritanceRuleResponse cancelInheritanceRule(UUID ruleId, UUID cancelledBy);

    // Inheritance Status Management
    /**
     * Get inheritance status for a recipient
     */
    List<InheritanceStatusResponse> getInheritanceStatusByRecipientId(UUID recipientId);

    /**
     * Get inheritance status for a recipient with pagination
     */
    Page<InheritanceStatusResponse> getInheritanceStatusByRecipientId(UUID recipientId, Pageable pageable);

    /**
     * Get inheritance status for a content
     */
    List<InheritanceStatusResponse> getInheritanceStatusByContentId(UUID contentId);

    /**
     * Get inheritance status for a content with pagination
     */
    Page<InheritanceStatusResponse> getInheritanceStatusByContentId(UUID contentId, Pageable pageable);

    /**
     * Get inheritance status for an inheritance rule
     */
    List<InheritanceStatusResponse> getInheritanceStatusByRuleId(UUID ruleId);

    /**
     * Get inheritance status for an inheritance rule with pagination
     */
    Page<InheritanceStatusResponse> getInheritanceStatusByRuleId(UUID ruleId, Pageable pageable);

    /**
     * Get inheritance status by status
     */
    List<InheritanceStatusResponse> getInheritanceStatusByStatus(com.legacykeep.legacy.entity.InheritanceStatus.Status status);

    /**
     * Get inheritance status by status with pagination
     */
    Page<InheritanceStatusResponse> getInheritanceStatusByStatus(com.legacykeep.legacy.entity.InheritanceStatus.Status status, Pageable pageable);

    /**
     * Mark content as accessed by recipient
     */
    InheritanceStatusResponse markContentAsAccessed(UUID recipientId, UUID contentId, UUID ruleId);

    /**
     * Decline inheritance by recipient
     */
    InheritanceStatusResponse declineInheritance(UUID recipientId, UUID contentId, UUID ruleId);

    // Inheritance Processing
    /**
     * Process inheritance for a specific rule
     */
    void processInheritance(UUID ruleId);

    /**
     * Process inheritance for a specific rule and recipient
     */
    void processInheritance(UUID ruleId, UUID recipientId);

    /**
     * Process inheritance for all active rules
     */
    void processAllActiveInheritance();

    /**
     * Process inheritance for a specific relationship type
     */
    void processInheritanceForRelationshipType(String relationshipType);

    /**
     * Process inheritance for a specific relationship type and value
     */
    void processInheritanceForTarget(TargetType targetType, String targetValue);

    // Event Management
    /**
     * Get inheritance events for a rule
     */
    List<InheritanceEventResponse> getInheritanceEventsByRuleId(UUID ruleId);

    /**
     * Get inheritance events for a rule with pagination
     */
    Page<InheritanceEventResponse> getInheritanceEventsByRuleId(UUID ruleId, Pageable pageable);

    /**
     * Get inheritance events by event type
     */
    List<InheritanceEventResponse> getInheritanceEventsByEventType(com.legacykeep.legacy.entity.InheritanceEvent.EventType eventType);

    /**
     * Get inheritance events by event type with pagination
     */
    Page<InheritanceEventResponse> getInheritanceEventsByEventType(com.legacykeep.legacy.entity.InheritanceEvent.EventType eventType, Pageable pageable);

    // Analytics and Statistics
    /**
     * Get inheritance analytics
     */
    Map<String, Object> getInheritanceAnalytics(UUID userId, LocalDateTime fromDate, LocalDateTime toDate);

    /**
     * Get inheritance statistics for a content
     */
    Map<String, Object> getInheritanceStatisticsForContent(UUID contentId);

    /**
     * Get inheritance statistics for a user
     */
    Map<String, Object> getInheritanceStatisticsForUser(UUID userId);

    /**
     * Get inheritance statistics for a relationship type
     */
    Map<String, Object> getInheritanceStatisticsForRelationshipType(String relationshipType);

    // Utility Methods
    /**
     * Check if user has access to content through inheritance
     */
    boolean hasInheritanceAccess(UUID userId, UUID contentId);

    /**
     * Get all content accessible through inheritance for a user
     */
    List<UUID> getInheritedContentIds(UUID userId);

    /**
     * Get all users who should inherit content based on rules
     */
    List<UUID> getEligibleRecipients(UUID contentId);

    /**
     * Validate inheritance rule
     */
    boolean validateInheritanceRule(CreateInheritanceRuleRequest request);

    // Relationship Service Integration Methods
    
    /**
     * Create inheritance rule targeting users by relationship type
     * 
     * @param contentId The content to inherit
     * @param creatorId The creator of the inheritance rule
     * @param relationshipTypeName The relationship type (e.g., "Son", "Daughter", "Father", "Mother")
     * @param inheritanceTrigger The trigger for inheritance
     * @param triggerMetadata Additional trigger metadata
     * @param priority Priority of the rule
     * @return Created inheritance rule response
     */
    InheritanceRuleResponse createRelationshipBasedInheritanceRule(
            UUID contentId, 
            UUID creatorId, 
            String relationshipTypeName, 
            InheritanceTrigger inheritanceTrigger, 
            Map<String, Object> triggerMetadata, 
            Integer priority);

    /**
     * Create inheritance rule targeting users by relationship category
     * 
     * @param contentId The content to inherit
     * @param creatorId The creator of the inheritance rule
     * @param relationshipCategory The relationship category (FAMILY, SOCIAL, PROFESSIONAL, CUSTOM)
     * @param inheritanceTrigger The trigger for inheritance
     * @param triggerMetadata Additional trigger metadata
     * @param priority Priority of the rule
     * @return Created inheritance rule response
     */
    InheritanceRuleResponse createCategoryBasedInheritanceRule(
            UUID contentId, 
            UUID creatorId, 
            String relationshipCategory, 
            InheritanceTrigger inheritanceTrigger, 
            Map<String, Object> triggerMetadata, 
            Integer priority);

    /**
     * Get all users who should inherit content based on relationship type
     * 
     * @param contentId The content ID
     * @param relationshipTypeName The relationship type name
     * @return List of user IDs who should inherit the content
     */
    List<UUID> getEligibleRecipientsByRelationshipType(UUID contentId, String relationshipTypeName);

    /**
     * Get all users who should inherit content based on relationship category
     * 
     * @param contentId The content ID
     * @param relationshipCategory The relationship category
     * @return List of user IDs who should inherit the content
     */
    List<UUID> getEligibleRecipientsByRelationshipCategory(UUID contentId, String relationshipCategory);

    /**
     * Process inheritance for all users with a specific relationship type to the content creator
     * 
     * @param contentId The content ID
     * @param relationshipTypeName The relationship type name
     */
    void processInheritanceForRelationshipType(UUID contentId, String relationshipTypeName);

    /**
     * Process inheritance for all users in a specific relationship category to the content creator
     * 
     * @param contentId The content ID
     * @param relationshipCategory The relationship category
     */
    void processInheritanceForRelationshipCategory(UUID contentId, String relationshipCategory);

    /**
     * Check if a user has inheritance access to content through relationship
     * 
     * @param userId The user ID
     * @param contentId The content ID
     * @param relationshipTypeName The required relationship type
     * @return True if user has access through the relationship
     */
    boolean hasInheritanceAccessByRelationshipType(UUID userId, UUID contentId, String relationshipTypeName);

    /**
     * Check if a user has inheritance access to content through relationship category
     * 
     * @param userId The user ID
     * @param contentId The content ID
     * @param relationshipCategory The required relationship category
     * @return True if user has access through the relationship category
     */
    boolean hasInheritanceAccessByRelationshipCategory(UUID userId, UUID contentId, String relationshipCategory);

    /**
     * Get all relationship types available for inheritance targeting
     * 
     * @return List of relationship type names
     */
    List<String> getAvailableRelationshipTypes();

    /**
     * Get all relationship categories available for inheritance targeting
     * 
     * @return List of relationship category names
     */
    List<String> getAvailableRelationshipCategories();
}
