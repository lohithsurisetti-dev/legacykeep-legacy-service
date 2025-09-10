package com.legacykeep.legacy.controller;

import com.legacykeep.legacy.dto.response.ApiResponse;
import com.legacykeep.legacy.dto.response.FamilyTreeResponse;
import com.legacykeep.legacy.service.FamilyTreeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for family tree visualization and management.
 * Provides endpoints for UI to display hierarchical family structure with content statistics.
 */
@Slf4j
@RestController
@RequestMapping("/family-tree")
@RequiredArgsConstructor
public class FamilyTreeController {

    private final FamilyTreeService familyTreeService;

    /**
     * Get complete family tree structure
     */
    @GetMapping("/family/{familyId}")
    public ResponseEntity<ApiResponse<FamilyTreeResponse>> getFamilyTree(
            @PathVariable UUID familyId,
            @RequestParam UUID requestingUserId) {
        
        log.info("Getting family tree for family: {} requested by user: {}", familyId, requestingUserId);
        
        FamilyTreeResponse response = familyTreeService.getFamilyTree(familyId, requestingUserId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Family tree retrieved successfully"));
    }

    /**
     * Get family tree from a specific user's perspective
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<FamilyTreeResponse>> getFamilyTreeForUser(
            @PathVariable UUID userId,
            @RequestParam UUID requestingUserId) {
        
        log.info("Getting family tree for user: {} requested by user: {}", userId, requestingUserId);
        
        FamilyTreeResponse response = familyTreeService.getFamilyTreeForUser(userId, requestingUserId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "User family tree retrieved successfully"));
    }

    /**
     * Get family tree with content filtering
     */
    @GetMapping("/family/{familyId}/filtered")
    public ResponseEntity<ApiResponse<FamilyTreeResponse>> getFamilyTreeWithFilters(
            @PathVariable UUID familyId,
            @RequestParam UUID requestingUserId,
            @RequestParam(defaultValue = "false") boolean includePrivateContent,
            @RequestParam(defaultValue = "false") boolean includeInactiveContent) {
        
        log.info("Getting filtered family tree for family: {} with private: {}, inactive: {}", 
                familyId, includePrivateContent, includeInactiveContent);
        
        FamilyTreeResponse response = familyTreeService.getFamilyTreeWithFilters(
                familyId, requestingUserId, includePrivateContent, includeInactiveContent);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Filtered family tree retrieved successfully"));
    }

    /**
     * Get family tree statistics summary
     */
    @GetMapping("/family/{familyId}/statistics")
    public ResponseEntity<ApiResponse<FamilyTreeResponse.PermissionSummary>> getFamilyTreeStatistics(
            @PathVariable UUID familyId,
            @RequestParam UUID requestingUserId) {
        
        log.info("Getting family tree statistics for family: {}", familyId);
        
        FamilyTreeResponse.PermissionSummary response = familyTreeService.getFamilyTreeStatistics(familyId, requestingUserId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Family tree statistics retrieved successfully"));
    }

    /**
     * Get content distribution across family members
     */
    @GetMapping("/family/{familyId}/content-distribution")
    public ResponseEntity<ApiResponse<FamilyTreeResponse>> getContentDistribution(
            @PathVariable UUID familyId,
            @RequestParam UUID requestingUserId) {
        
        log.info("Getting content distribution for family: {}", familyId);
        
        FamilyTreeResponse response = familyTreeService.getContentDistribution(familyId, requestingUserId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Content distribution retrieved successfully"));
    }

    /**
     * Get family tree with relationship details
     */
    @GetMapping("/family/{familyId}/relationships")
    public ResponseEntity<ApiResponse<FamilyTreeResponse>> getFamilyTreeWithRelationships(
            @PathVariable UUID familyId,
            @RequestParam UUID requestingUserId,
            @RequestParam(defaultValue = "true") boolean includeRelationshipDetails) {
        
        log.info("Getting family tree with relationships for family: {}", familyId);
        
        FamilyTreeResponse response = familyTreeService.getFamilyTreeWithRelationships(
                familyId, requestingUserId, includeRelationshipDetails);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Family tree with relationships retrieved successfully"));
    }

    /**
     * Get family tree for a specific generation
     */
    @GetMapping("/family/{familyId}/generation/{generationLevel}")
    public ResponseEntity<ApiResponse<FamilyTreeResponse>> getFamilyTreeByGeneration(
            @PathVariable UUID familyId,
            @PathVariable Integer generationLevel,
            @RequestParam UUID requestingUserId) {
        
        log.info("Getting family tree for generation: {} in family: {}", generationLevel, familyId);
        
        FamilyTreeResponse response = familyTreeService.getFamilyTreeByGeneration(
                familyId, generationLevel, requestingUserId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Generation family tree retrieved successfully"));
    }

    /**
     * Get family tree with inheritance information
     */
    @GetMapping("/family/{familyId}/inheritance")
    public ResponseEntity<ApiResponse<FamilyTreeResponse>> getFamilyTreeWithInheritance(
            @PathVariable UUID familyId,
            @RequestParam UUID requestingUserId) {
        
        log.info("Getting family tree with inheritance for family: {}", familyId);
        
        FamilyTreeResponse response = familyTreeService.getFamilyTreeWithInheritance(familyId, requestingUserId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Family tree with inheritance retrieved successfully"));
    }

    /**
     * Get family tree with permission matrix
     */
    @GetMapping("/family/{familyId}/permissions")
    public ResponseEntity<ApiResponse<FamilyTreeResponse>> getFamilyTreeWithPermissions(
            @PathVariable UUID familyId,
            @RequestParam UUID requestingUserId) {
        
        log.info("Getting family tree with permissions for family: {}", familyId);
        
        FamilyTreeResponse response = familyTreeService.getFamilyTreeWithPermissions(familyId, requestingUserId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Family tree with permissions retrieved successfully"));
    }

    /**
     * Export family tree data
     */
    @GetMapping("/family/{familyId}/export")
    public ResponseEntity<ApiResponse<String>> exportFamilyTree(
            @PathVariable UUID familyId,
            @RequestParam UUID requestingUserId,
            @RequestParam(defaultValue = "JSON") String format) {
        
        log.info("Exporting family tree for family: {} in format: {}", familyId, format);
        
        String response = familyTreeService.exportFamilyTree(familyId, requestingUserId, format);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Family tree exported successfully"));
    }

    /**
     * Get family tree visualization data (optimized for UI rendering)
     */
    @GetMapping("/family/{familyId}/visualization")
    public ResponseEntity<ApiResponse<FamilyTreeResponse>> getFamilyTreeVisualization(
            @PathVariable UUID familyId,
            @RequestParam UUID requestingUserId,
            @RequestParam(defaultValue = "false") boolean includeContentStats,
            @RequestParam(defaultValue = "false") boolean includePermissions,
            @RequestParam(defaultValue = "false") boolean includeRelationships) {
        
        log.info("Getting family tree visualization for family: {} with stats: {}, permissions: {}, relationships: {}", 
                familyId, includeContentStats, includePermissions, includeRelationships);
        
        // This would be an optimized endpoint that combines multiple data sources
        // for efficient UI rendering
        FamilyTreeResponse response = familyTreeService.getFamilyTree(familyId, requestingUserId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Family tree visualization data retrieved successfully"));
    }
}
