package com.legacykeep.legacy.controller;

import com.legacykeep.legacy.dto.request.CreateInheritanceRuleRequest;
import com.legacykeep.legacy.dto.request.UpdateInheritanceRuleRequest;
import com.legacykeep.legacy.dto.response.ApiResponse;
import com.legacykeep.legacy.dto.response.InheritanceRuleResponse;
import com.legacykeep.legacy.dto.response.InheritanceStatusResponse;
import com.legacykeep.legacy.entity.InheritanceStatus;
import com.legacykeep.legacy.enums.InheritanceTrigger;
import com.legacykeep.legacy.enums.TargetType;
import com.legacykeep.legacy.service.InheritanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for inheritance management.
 * Provides endpoints for managing inheritance rules and status tracking.
 */
@RestController
@RequestMapping("/inheritance")
@RequiredArgsConstructor
@Slf4j
public class InheritanceController {

    private final InheritanceService inheritanceService;

    // Inheritance Rule Management
    /**
     * Create a new inheritance rule
     */
    @PostMapping("/rules")
    public ResponseEntity<ApiResponse<InheritanceRuleResponse>> createInheritanceRule(
            @Valid @RequestBody CreateInheritanceRuleRequest request,
            Authentication authentication) {
        
        log.info("Creating inheritance rule for content: {}", request.getContentId());
        
        // Use a default creator ID for testing when authentication is not available
        UUID creatorId = authentication != null ? 
            UUID.fromString(authentication.getName()) : 
            UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        
        InheritanceRuleResponse response = inheritanceService.createInheritanceRule(request, creatorId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Inheritance rule created successfully"));
    }

    /**
     * Update an existing inheritance rule
     */
    @PutMapping("/rules/{ruleId}")
    public ResponseEntity<ApiResponse<InheritanceRuleResponse>> updateInheritanceRule(
            @PathVariable UUID ruleId,
            @Valid @RequestBody UpdateInheritanceRuleRequest request,
            Authentication authentication) {
        
        log.info("Updating inheritance rule: {}", ruleId);
        
        // Use a default user ID for testing when authentication is not available
        UUID updatedBy = authentication != null ? 
            UUID.fromString(authentication.getName()) : 
            UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        
        InheritanceRuleResponse response = inheritanceService.updateInheritanceRule(ruleId, request, updatedBy);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Inheritance rule updated successfully"));
    }

    /**
     * Get inheritance rule by ID
     */
    @GetMapping("/rules/{ruleId}")
    public ResponseEntity<ApiResponse<InheritanceRuleResponse>> getInheritanceRuleById(
            @PathVariable UUID ruleId) {
        
        log.debug("Getting inheritance rule by ID: {}", ruleId);
        
        InheritanceRuleResponse response = inheritanceService.getInheritanceRuleById(ruleId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Inheritance rule retrieved successfully"));
    }

    /**
     * Get all inheritance rules for a content
     */
    @GetMapping("/content/{contentId}/rules")
    public ResponseEntity<ApiResponse<List<InheritanceRuleResponse>>> getInheritanceRulesByContentId(
            @PathVariable UUID contentId) {
        
        log.debug("Getting inheritance rules for content: {}", contentId);
        
        List<InheritanceRuleResponse> response = inheritanceService.getInheritanceRulesByContentId(contentId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Inheritance rules retrieved successfully"));
    }

    /**
     * Get all inheritance rules for a content with pagination
     */
    @GetMapping("/content/{contentId}/rules/paginated")
    public ResponseEntity<ApiResponse<Page<InheritanceRuleResponse>>> getInheritanceRulesByContentIdPaginated(
            @PathVariable UUID contentId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting inheritance rules for content: {} with pagination", contentId);
        
        Page<InheritanceRuleResponse> response = inheritanceService.getInheritanceRulesByContentId(contentId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Inheritance rules retrieved successfully"));
    }

    /**
     * Get all inheritance rules for a creator
     */
    @GetMapping("/creator/{creatorId}/rules")
    public ResponseEntity<ApiResponse<List<InheritanceRuleResponse>>> getInheritanceRulesByCreatorId(
            @PathVariable UUID creatorId) {
        
        log.debug("Getting inheritance rules for creator: {}", creatorId);
        
        List<InheritanceRuleResponse> response = inheritanceService.getInheritanceRulesByCreatorId(creatorId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Inheritance rules retrieved successfully"));
    }

    /**
     * Get inheritance rules by status
     */
    @GetMapping("/rules/status/{status}")
    public ResponseEntity<ApiResponse<List<InheritanceRuleResponse>>> getInheritanceRulesByStatus(
            @PathVariable com.legacykeep.legacy.enums.InheritanceStatus status) {
        
        log.debug("Getting inheritance rules by status: {}", status);
        
        List<InheritanceRuleResponse> response = inheritanceService.getInheritanceRulesByStatus(status);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Inheritance rules retrieved successfully"));
    }

    /**
     * Get inheritance rules by target type and value
     */
    @GetMapping("/rules/target/{targetType}/{targetValue}")
    public ResponseEntity<ApiResponse<List<InheritanceRuleResponse>>> getInheritanceRulesByTarget(
            @PathVariable TargetType targetType,
            @PathVariable String targetValue) {
        
        log.debug("Getting inheritance rules by target: {} - {}", targetType, targetValue);
        
        List<InheritanceRuleResponse> response = inheritanceService.getInheritanceRulesByTarget(targetType, targetValue);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Inheritance rules retrieved successfully"));
    }

    /**
     * Delete an inheritance rule
     */
    @DeleteMapping("/rules/{ruleId}")
    public ResponseEntity<ApiResponse<Void>> deleteInheritanceRule(
            @PathVariable UUID ruleId,
            Authentication authentication) {
        
        log.info("Deleting inheritance rule: {}", ruleId);
        
        // Use a default user ID for testing when authentication is not available
        UUID deletedBy = authentication != null ? 
            UUID.fromString(authentication.getName()) : 
            UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        
        inheritanceService.deleteInheritanceRule(ruleId, deletedBy);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Inheritance rule deleted successfully"));
    }

    /**
     * Activate an inheritance rule
     */
    @PostMapping("/rules/{ruleId}/activate")
    public ResponseEntity<ApiResponse<InheritanceRuleResponse>> activateInheritanceRule(
            @PathVariable UUID ruleId,
            Authentication authentication) {
        
        log.info("Activating inheritance rule: {}", ruleId);
        
        // Use a default user ID for testing when authentication is not available
        UUID activatedBy = authentication != null ? 
            UUID.fromString(authentication.getName()) : 
            UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        
        InheritanceRuleResponse response = inheritanceService.activateInheritanceRule(ruleId, activatedBy);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Inheritance rule activated successfully"));
    }

    /**
     * Pause an inheritance rule
     */
    @PostMapping("/rules/{ruleId}/pause")
    public ResponseEntity<ApiResponse<InheritanceRuleResponse>> pauseInheritanceRule(
            @PathVariable UUID ruleId,
            Authentication authentication) {
        
        log.info("Pausing inheritance rule: {}", ruleId);
        
        // Use a default user ID for testing when authentication is not available
        UUID pausedBy = authentication != null ? 
            UUID.fromString(authentication.getName()) : 
            UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        
        InheritanceRuleResponse response = inheritanceService.pauseInheritanceRule(ruleId, pausedBy);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Inheritance rule paused successfully"));
    }

    // Inheritance Status Management
    /**
     * Get inheritance status for a recipient
     */
    @GetMapping("/recipient/{recipientId}/status")
    public ResponseEntity<ApiResponse<List<InheritanceStatusResponse>>> getInheritanceStatusByRecipientId(
            @PathVariable UUID recipientId) {
        
        log.debug("Getting inheritance status for recipient: {}", recipientId);
        
        List<InheritanceStatusResponse> response = inheritanceService.getInheritanceStatusByRecipientId(recipientId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Inheritance status retrieved successfully"));
    }

    /**
     * Get inheritance status for a content
     */
    @GetMapping("/content/{contentId}/status")
    public ResponseEntity<ApiResponse<List<InheritanceStatusResponse>>> getInheritanceStatusByContentId(
            @PathVariable UUID contentId) {
        
        log.debug("Getting inheritance status for content: {}", contentId);
        
        List<InheritanceStatusResponse> response = inheritanceService.getInheritanceStatusByContentId(contentId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Inheritance status retrieved successfully"));
    }

    /**
     * Mark content as accessed by recipient
     */
    @PostMapping("/content/{contentId}/access/{recipientId}")
    public ResponseEntity<ApiResponse<InheritanceStatusResponse>> markContentAsAccessed(
            @PathVariable UUID contentId,
            @PathVariable UUID recipientId,
            @RequestParam UUID ruleId) {
        
        log.info("Marking content as accessed by recipient: {} for content: {}", recipientId, contentId);
        
        InheritanceStatusResponse response = inheritanceService.markContentAsAccessed(recipientId, contentId, ruleId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Content marked as accessed successfully"));
    }

    /**
     * Decline inheritance by recipient
     */
    @PostMapping("/content/{contentId}/decline/{recipientId}")
    public ResponseEntity<ApiResponse<InheritanceStatusResponse>> declineInheritance(
            @PathVariable UUID contentId,
            @PathVariable UUID recipientId,
            @RequestParam UUID ruleId) {
        
        log.info("Declining inheritance by recipient: {} for content: {}", recipientId, contentId);
        
        InheritanceStatusResponse response = inheritanceService.declineInheritance(recipientId, contentId, ruleId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Inheritance declined successfully"));
    }

    // Inheritance Processing
    /**
     * Process inheritance for a specific rule
     */
    @PostMapping("/rules/{ruleId}/process")
    public ResponseEntity<ApiResponse<Void>> processInheritance(
            @PathVariable UUID ruleId) {
        
        log.info("Processing inheritance for rule: {}", ruleId);
        
        inheritanceService.processInheritance(ruleId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Inheritance processing initiated successfully"));
    }

    /**
     * Process inheritance for all active rules
     */
    @PostMapping("/process/all")
    public ResponseEntity<ApiResponse<Void>> processAllActiveInheritance() {
        
        log.info("Processing all active inheritance rules");
        
        inheritanceService.processAllActiveInheritance();
        
        return ResponseEntity.ok(ApiResponse.success(null, "All active inheritance processing initiated successfully"));
    }

    /**
     * Process inheritance for a specific relationship type
     */
    @PostMapping("/process/relationship-type/{relationshipType}")
    public ResponseEntity<ApiResponse<Void>> processInheritanceForRelationshipType(
            @PathVariable String relationshipType) {
        
        log.info("Processing inheritance for relationship type: {}", relationshipType);
        
        inheritanceService.processInheritanceForRelationshipType(relationshipType);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Inheritance processing initiated successfully"));
    }

    // Utility Methods
    /**
     * Check if user has access to content through inheritance
     */
    @GetMapping("/access/{userId}/{contentId}")
    public ResponseEntity<ApiResponse<Boolean>> hasInheritanceAccess(
            @PathVariable UUID userId,
            @PathVariable UUID contentId) {
        
        log.debug("Checking inheritance access for user: {} to content: {}", userId, contentId);
        
        boolean hasAccess = inheritanceService.hasInheritanceAccess(userId, contentId);
        
        return ResponseEntity.ok(ApiResponse.success(hasAccess, "Inheritance access check completed"));
    }

    /**
     * Get all content accessible through inheritance for a user
     */
    @GetMapping("/user/{userId}/inherited-content")
    public ResponseEntity<ApiResponse<List<UUID>>> getInheritedContentIds(
            @PathVariable UUID userId) {
        
        log.debug("Getting inherited content IDs for user: {}", userId);
        
        List<UUID> response = inheritanceService.getInheritedContentIds(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Inherited content IDs retrieved successfully"));
    }

    /**
     * Get all users who should inherit content based on rules
     */
    @GetMapping("/content/{contentId}/eligible-recipients")
    public ResponseEntity<ApiResponse<List<UUID>>> getEligibleRecipients(
            @PathVariable UUID contentId) {
        
        log.debug("Getting eligible recipients for content: {}", contentId);
        
        List<UUID> response = inheritanceService.getEligibleRecipients(contentId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Eligible recipients retrieved successfully"));
    }

    // Relationship Service Integration Endpoints

    /**
     * Create inheritance rule targeting users by relationship type
     */
    @PostMapping("/rules/relationship-type")
    public ResponseEntity<ApiResponse<InheritanceRuleResponse>> createRelationshipBasedInheritanceRule(
            @RequestBody Map<String, Object> requestBody,
            Authentication authentication) {
        
        // Extract parameters from request body
        UUID contentId = UUID.fromString((String) requestBody.get("contentId"));
        String relationshipTypeName = (String) requestBody.get("relationshipTypeName");
        InheritanceTrigger inheritanceTrigger = InheritanceTrigger.valueOf((String) requestBody.get("inheritanceTrigger"));
        @SuppressWarnings("unchecked")
        Map<String, Object> triggerMetadata = (Map<String, Object>) requestBody.get("triggerMetadata");
        Integer priority = (Integer) requestBody.get("priority");
        
        log.info("Creating relationship-based inheritance rule for content: {} targeting relationship type: {}", 
                contentId, relationshipTypeName);
        
        // Use a default user ID for testing when authentication is not available
        UUID creatorId = authentication != null ? 
            UUID.fromString(authentication.getName()) : 
            UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        
        InheritanceRuleResponse response = inheritanceService.createRelationshipBasedInheritanceRule(
                contentId, creatorId, relationshipTypeName, inheritanceTrigger, triggerMetadata, priority);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Relationship-based inheritance rule created successfully"));
    }

    /**
     * Create inheritance rule targeting users by relationship category
     */
    @PostMapping("/rules/relationship-category")
    public ResponseEntity<ApiResponse<InheritanceRuleResponse>> createCategoryBasedInheritanceRule(
            @RequestBody Map<String, Object> requestBody,
            Authentication authentication) {
        
        // Extract parameters from request body
        UUID contentId = UUID.fromString((String) requestBody.get("contentId"));
        String relationshipCategory = (String) requestBody.get("relationshipCategory");
        InheritanceTrigger inheritanceTrigger = InheritanceTrigger.valueOf((String) requestBody.get("inheritanceTrigger"));
        @SuppressWarnings("unchecked")
        Map<String, Object> triggerMetadata = (Map<String, Object>) requestBody.get("triggerMetadata");
        Integer priority = (Integer) requestBody.get("priority");
        
        log.info("Creating category-based inheritance rule for content: {} targeting relationship category: {}", 
                contentId, relationshipCategory);
        
        // Use a default user ID for testing when authentication is not available
        UUID creatorId = authentication != null ? 
            UUID.fromString(authentication.getName()) : 
            UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        
        InheritanceRuleResponse response = inheritanceService.createCategoryBasedInheritanceRule(
                contentId, creatorId, relationshipCategory, inheritanceTrigger, triggerMetadata, priority);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Category-based inheritance rule created successfully"));
    }

    /**
     * Get eligible recipients by relationship type
     */
    @GetMapping("/content/{contentId}/eligible-recipients/relationship-type/{relationshipTypeName}")
    public ResponseEntity<ApiResponse<List<UUID>>> getEligibleRecipientsByRelationshipType(
            @PathVariable UUID contentId,
            @PathVariable String relationshipTypeName) {
        
        log.debug("Getting eligible recipients by relationship type '{}' for content: {}", 
                 relationshipTypeName, contentId);
        
        List<UUID> response = inheritanceService.getEligibleRecipientsByRelationshipType(contentId, relationshipTypeName);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Eligible recipients retrieved successfully"));
    }

    /**
     * Get eligible recipients by relationship category
     */
    @GetMapping("/content/{contentId}/eligible-recipients/relationship-category/{relationshipCategory}")
    public ResponseEntity<ApiResponse<List<UUID>>> getEligibleRecipientsByRelationshipCategory(
            @PathVariable UUID contentId,
            @PathVariable String relationshipCategory) {
        
        log.debug("Getting eligible recipients by relationship category '{}' for content: {}", 
                 relationshipCategory, contentId);
        
        List<UUID> response = inheritanceService.getEligibleRecipientsByRelationshipCategory(contentId, relationshipCategory);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Eligible recipients retrieved successfully"));
    }

    /**
     * Process inheritance for relationship type
     */
    @PostMapping("/content/{contentId}/process/relationship-type/{relationshipTypeName}")
    public ResponseEntity<ApiResponse<Void>> processInheritanceForRelationshipType(
            @PathVariable UUID contentId,
            @PathVariable String relationshipTypeName) {
        
        log.info("Processing inheritance for relationship type '{}' for content: {}", 
                relationshipTypeName, contentId);
        
        inheritanceService.processInheritanceForRelationshipType(contentId, relationshipTypeName);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Inheritance processing initiated successfully"));
    }

    /**
     * Process inheritance for relationship category
     */
    @PostMapping("/content/{contentId}/process/relationship-category/{relationshipCategory}")
    public ResponseEntity<ApiResponse<Void>> processInheritanceForRelationshipCategory(
            @PathVariable UUID contentId,
            @PathVariable String relationshipCategory) {
        
        log.info("Processing inheritance for relationship category '{}' for content: {}", 
                relationshipCategory, contentId);
        
        inheritanceService.processInheritanceForRelationshipCategory(contentId, relationshipCategory);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Inheritance processing initiated successfully"));
    }

    /**
     * Check inheritance access by relationship type
     */
    @GetMapping("/access/{userId}/{contentId}/relationship-type/{relationshipTypeName}")
    public ResponseEntity<ApiResponse<Boolean>> hasInheritanceAccessByRelationshipType(
            @PathVariable UUID userId,
            @PathVariable UUID contentId,
            @PathVariable String relationshipTypeName) {
        
        log.debug("Checking inheritance access by relationship type '{}' for user: {} to content: {}", 
                 relationshipTypeName, userId, contentId);
        
        boolean hasAccess = inheritanceService.hasInheritanceAccessByRelationshipType(userId, contentId, relationshipTypeName);
        
        return ResponseEntity.ok(ApiResponse.success(hasAccess, "Inheritance access check completed"));
    }

    /**
     * Check inheritance access by relationship category
     */
    @GetMapping("/access/{userId}/{contentId}/relationship-category/{relationshipCategory}")
    public ResponseEntity<ApiResponse<Boolean>> hasInheritanceAccessByRelationshipCategory(
            @PathVariable UUID userId,
            @PathVariable UUID contentId,
            @PathVariable String relationshipCategory) {
        
        log.debug("Checking inheritance access by relationship category '{}' for user: {} to content: {}", 
                 relationshipCategory, userId, contentId);
        
        boolean hasAccess = inheritanceService.hasInheritanceAccessByRelationshipCategory(userId, contentId, relationshipCategory);
        
        return ResponseEntity.ok(ApiResponse.success(hasAccess, "Inheritance access check completed"));
    }

    /**
     * Get available relationship types
     */
    @GetMapping("/relationship-types")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableRelationshipTypes() {
        
        log.debug("Getting available relationship types");
        
        List<String> response = inheritanceService.getAvailableRelationshipTypes();
        
        return ResponseEntity.ok(ApiResponse.success(response, "Available relationship types retrieved successfully"));
    }

    /**
     * Get available relationship categories
     */
    @GetMapping("/relationship-categories")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableRelationshipCategories() {
        
        log.debug("Getting available relationship categories");
        
        List<String> response = inheritanceService.getAvailableRelationshipCategories();
        
        return ResponseEntity.ok(ApiResponse.success(response, "Available relationship categories retrieved successfully"));
    }
}
