package com.legacykeep.legacy.controller;

import com.legacykeep.legacy.dto.response.ApiResponse;
import com.legacykeep.legacy.entity.LegacyContent;
import com.legacykeep.legacy.entity.LegacyRecipient;
import com.legacykeep.legacy.service.LegacyContentService;
import com.legacykeep.legacy.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for managing content permissions and access control.
 * Handles generation-based access control, privacy settings, and content sharing controls.
 */
@Slf4j
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;
    private final LegacyContentService contentService;

    /**
     * Check if user has access to content
     */
    @GetMapping("/content/{contentId}/access/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> checkContentAccess(
            @PathVariable UUID contentId,
            @PathVariable UUID userId,
            @RequestParam(required = false) Integer userGenerationLevel,
            @RequestParam(defaultValue = "false") boolean isFamilyMember,
            @RequestParam(defaultValue = "false") boolean isExtendedFamilyMember) {
        
        log.info("Checking content access for user: {} to content: {}", userId, contentId);
        
        LegacyContent content = contentService.getContentEntityById(contentId);
        boolean hasAccess = permissionService.hasContentAccess(
                userId, content, userGenerationLevel, isFamilyMember, isExtendedFamilyMember);
        
        return ResponseEntity.ok(ApiResponse.success(hasAccess, "Content access check completed"));
    }

    /**
     * Check if user has specific access level to content
     */
    @GetMapping("/content/{contentId}/access-level/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> checkAccessLevel(
            @PathVariable UUID contentId,
            @PathVariable UUID userId,
            @RequestParam LegacyRecipient.AccessLevel requiredAccessLevel,
            @RequestParam(required = false) Integer userGenerationLevel,
            @RequestParam(defaultValue = "false") boolean isFamilyMember,
            @RequestParam(defaultValue = "false") boolean isExtendedFamilyMember) {
        
        log.info("Checking access level {} for user: {} to content: {}", requiredAccessLevel, userId, contentId);
        
        LegacyContent content = contentService.getContentEntityById(contentId);
        boolean hasAccessLevel = permissionService.hasAccessLevel(
                userId, content, requiredAccessLevel, userGenerationLevel, isFamilyMember, isExtendedFamilyMember);
        
        return ResponseEntity.ok(ApiResponse.success(hasAccessLevel, "Access level check completed"));
    }

    /**
     * Get effective access level for user on content
     */
    @GetMapping("/content/{contentId}/effective-access/{userId}")
    public ResponseEntity<ApiResponse<LegacyRecipient.AccessLevel>> getEffectiveAccessLevel(
            @PathVariable UUID contentId,
            @PathVariable UUID userId,
            @RequestParam(required = false) Integer userGenerationLevel,
            @RequestParam(defaultValue = "false") boolean isFamilyMember,
            @RequestParam(defaultValue = "false") boolean isExtendedFamilyMember) {
        
        log.info("Getting effective access level for user: {} to content: {}", userId, contentId);
        
        LegacyContent content = contentService.getContentEntityById(contentId);
        LegacyRecipient.AccessLevel effectiveAccessLevel = permissionService.getEffectiveAccessLevel(
                userId, content, userGenerationLevel, isFamilyMember, isExtendedFamilyMember);
        
        return ResponseEntity.ok(ApiResponse.success(effectiveAccessLevel, "Effective access level retrieved"));
    }

    /**
     * Check if content can be inherited by generation
     */
    @GetMapping("/content/{contentId}/inheritance/{targetGenerationLevel}")
    public ResponseEntity<ApiResponse<Boolean>> checkInheritanceEligibility(
            @PathVariable UUID contentId,
            @PathVariable Integer targetGenerationLevel) {
        
        log.info("Checking inheritance eligibility for content: {} to generation: {}", 
                contentId, targetGenerationLevel);
        
        LegacyContent content = contentService.getContentEntityById(contentId);
        boolean canInherit = permissionService.canInheritContent(content, targetGenerationLevel);
        
        return ResponseEntity.ok(ApiResponse.success(canInherit, "Inheritance eligibility check completed"));
    }

    /**
     * Get accessible users for content
     */
    @PostMapping("/content/{contentId}/accessible-users")
    public ResponseEntity<ApiResponse<List<UUID>>> getAccessibleUsers(
            @PathVariable UUID contentId,
            @RequestBody Map<String, Object> requestBody) {
        
        log.info("Getting accessible users for content: {}", contentId);
        
        LegacyContent content = contentService.getContentEntityById(contentId);
        
        @SuppressWarnings("unchecked")
        List<UUID> familyMembers = (List<UUID>) requestBody.get("familyMembers");
        @SuppressWarnings("unchecked")
        List<UUID> extendedFamilyMembers = (List<UUID>) requestBody.get("extendedFamilyMembers");
        
        List<UUID> accessibleUsers = permissionService.getAccessibleUsers(
                content, familyMembers, extendedFamilyMembers);
        
        return ResponseEntity.ok(ApiResponse.success(accessibleUsers, "Accessible users retrieved"));
    }

    /**
     * Validate privacy level inheritance
     */
    @GetMapping("/privacy-inheritance/validate")
    public ResponseEntity<ApiResponse<Boolean>> validatePrivacyInheritance(
            @RequestParam LegacyContent.PrivacyLevel parentPrivacyLevel,
            @RequestParam LegacyContent.PrivacyLevel childPrivacyLevel) {
        
        log.info("Validating privacy inheritance from {} to {}", parentPrivacyLevel, childPrivacyLevel);
        
        boolean isValid = permissionService.validatePrivacyInheritance(parentPrivacyLevel, childPrivacyLevel);
        
        return ResponseEntity.ok(ApiResponse.success(isValid, "Privacy inheritance validation completed"));
    }

    /**
     * Get default privacy level for generation
     */
    @GetMapping("/privacy-level/default/{generationLevel}")
    public ResponseEntity<ApiResponse<LegacyContent.PrivacyLevel>> getDefaultPrivacyLevel(
            @PathVariable Integer generationLevel) {
        
        log.info("Getting default privacy level for generation: {}", generationLevel);
        
        LegacyContent.PrivacyLevel defaultPrivacyLevel = permissionService.getDefaultPrivacyLevelForGeneration(generationLevel);
        
        return ResponseEntity.ok(ApiResponse.success(defaultPrivacyLevel, "Default privacy level retrieved"));
    }

    /**
     * Check if user can modify privacy level
     */
    @GetMapping("/content/{contentId}/privacy-modification/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> canModifyPrivacyLevel(
            @PathVariable UUID contentId,
            @PathVariable UUID userId,
            @RequestParam LegacyContent.PrivacyLevel newPrivacyLevel) {
        
        log.info("Checking if user {} can modify privacy level of content {} to {}", 
                userId, contentId, newPrivacyLevel);
        
        LegacyContent content = contentService.getContentEntityById(contentId);
        boolean canModify = permissionService.canModifyPrivacyLevel(userId, content, newPrivacyLevel);
        
        return ResponseEntity.ok(ApiResponse.success(canModify, "Privacy modification permission check completed"));
    }

    /**
     * Check content visibility to generation
     */
    @GetMapping("/content-visibility")
    public ResponseEntity<ApiResponse<Boolean>> checkContentVisibility(
            @RequestParam Integer contentGenerationLevel,
            @RequestParam Integer userGenerationLevel) {
        
        log.info("Checking content visibility: content generation {} to user generation {}", 
                contentGenerationLevel, userGenerationLevel);
        
        boolean isVisible = permissionService.isContentVisibleToGeneration(
                contentGenerationLevel, userGenerationLevel);
        
        return ResponseEntity.ok(ApiResponse.success(isVisible, "Content visibility check completed"));
    }

    /**
     * Check sharing permission between generations
     */
    @GetMapping("/sharing-permission")
    public ResponseEntity<ApiResponse<Boolean>> checkSharingPermission(
            @RequestParam Integer fromGeneration,
            @RequestParam Integer toGeneration) {
        
        log.info("Checking sharing permission from generation {} to generation {}", 
                fromGeneration, toGeneration);
        
        boolean isAllowed = permissionService.isSharingAllowedBetweenGenerations(fromGeneration, toGeneration);
        
        return ResponseEntity.ok(ApiResponse.success(isAllowed, "Sharing permission check completed"));
    }

    /**
     * Get content accessible to user with permission filtering
     */
    @GetMapping("/content/accessible/{userId}")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getAccessibleContent(
            @PathVariable UUID userId,
            @RequestParam(required = false) UUID familyId,
            @RequestParam(required = false) Integer userGenerationLevel,
            @RequestParam(defaultValue = "false") boolean isFamilyMember,
            @RequestParam(defaultValue = "false") boolean isExtendedFamilyMember,
            @RequestParam(required = false) LegacyRecipient.AccessLevel minimumAccessLevel,
            Pageable pageable) {
        
        log.info("Getting accessible content for user: {} with permission filtering", userId);
        
        // Get all content and filter by permissions
        Page<LegacyContent> contentPage = contentService.getAccessibleContentEntities(userId, familyId, pageable);
        
        // Filter content based on permissions
        Page<Map<String, Object>> accessibleContent = contentPage.map(content -> {
            boolean hasAccess = permissionService.hasContentAccess(
                    userId, content, userGenerationLevel, isFamilyMember, isExtendedFamilyMember);
            
            if (!hasAccess) {
                return null; // This will be filtered out
            }
            
            // Check minimum access level if specified
            if (minimumAccessLevel != null) {
                boolean hasMinimumAccess = permissionService.hasAccessLevel(
                        userId, content, minimumAccessLevel, userGenerationLevel, isFamilyMember, isExtendedFamilyMember);
                if (!hasMinimumAccess) {
                    return null; // This will be filtered out
                }
            }
            
            // Return content with permission info
            Map<String, Object> contentInfo = new HashMap<>();
            contentInfo.put("contentId", content.getId());
            contentInfo.put("title", content.getTitle());
            contentInfo.put("privacyLevel", content.getPrivacyLevel());
            contentInfo.put("generationLevel", content.getGenerationLevel());
            contentInfo.put("effectiveAccessLevel", permissionService.getEffectiveAccessLevel(
                    userId, content, userGenerationLevel, isFamilyMember, isExtendedFamilyMember));
            return contentInfo;
        }).map(content -> content); // Filter out nulls
        
        return ResponseEntity.ok(ApiResponse.success(accessibleContent, "Accessible content retrieved with permission filtering"));
    }
}
