package com.legacykeep.legacy.service.impl;

import com.legacykeep.legacy.entity.LegacyContent;
import com.legacykeep.legacy.entity.LegacyRecipient;
import com.legacykeep.legacy.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of PermissionService for managing content permissions and access control.
 * Handles generation-based access control, privacy settings, and content sharing controls.
 */
@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    @Override
    public boolean hasContentAccess(UUID userId, LegacyContent content, Integer userGenerationLevel, 
                                  boolean isFamilyMember, boolean isExtendedFamilyMember) {
        log.debug("Checking content access for user: {} to content: {} (privacy: {}, user generation: {})", 
                userId, content.getId(), content.getPrivacyLevel(), userGenerationLevel);

        // First check basic privacy level access
        boolean hasBasicAccess = content.hasAccess(userId, isFamilyMember, isExtendedFamilyMember);
        if (!hasBasicAccess) {
            log.debug("User {} denied access to content {} due to privacy level {}", 
                    userId, content.getId(), content.getPrivacyLevel());
            return false;
        }

        // Check generation-based visibility rules
        boolean isVisibleToGeneration = isContentVisibleToGeneration(
                content.getGenerationLevel(), userGenerationLevel);
        if (!isVisibleToGeneration) {
            log.debug("User {} denied access to content {} due to generation visibility rules", 
                    userId, content.getId());
            return false;
        }

        // Check if user is specifically granted access through recipients
        boolean hasRecipientAccess = hasRecipientAccess(userId, content);
        if (hasRecipientAccess) {
            log.debug("User {} granted access to content {} through recipient permissions", 
                    userId, content.getId());
            return true;
        }

        log.debug("User {} granted access to content {} through privacy and generation rules", 
                userId, content.getId());
        return true;
    }

    @Override
    public boolean hasAccessLevel(UUID userId, LegacyContent content, LegacyRecipient.AccessLevel requiredAccessLevel,
                                Integer userGenerationLevel, boolean isFamilyMember, boolean isExtendedFamilyMember) {
        log.debug("Checking access level {} for user: {} to content: {}", 
                requiredAccessLevel, userId, content.getId());

        // First check if user has basic content access
        if (!hasContentAccess(userId, content, userGenerationLevel, isFamilyMember, isExtendedFamilyMember)) {
            return false;
        }

        // Get the effective access level for the user
        LegacyRecipient.AccessLevel effectiveAccessLevel = getEffectiveAccessLevel(userId, content, userGenerationLevel, 
                isFamilyMember, isExtendedFamilyMember);

        // Check if effective access level meets the required level
        boolean hasRequiredAccess = effectiveAccessLevel.ordinal() >= requiredAccessLevel.ordinal();
        
        log.debug("User {} has effective access level {} (required: {}): {}", 
                userId, effectiveAccessLevel, requiredAccessLevel, hasRequiredAccess);
        
        return hasRequiredAccess;
    }

    @Override
    public LegacyRecipient.AccessLevel getEffectiveAccessLevel(UUID userId, LegacyContent content, Integer userGenerationLevel,
                                             boolean isFamilyMember, boolean isExtendedFamilyMember) {
        log.debug("Getting effective access level for user: {} to content: {}", userId, content.getId());

        // Check if user is the creator
        if (content.getCreatorId().equals(userId)) {
            log.debug("User {} is creator of content {}, granting EDIT access", userId, content.getId());
            return LegacyRecipient.AccessLevel.EDIT;
        }

        // Check recipient-specific access levels
        LegacyRecipient.AccessLevel recipientAccessLevel = getRecipientAccessLevel(userId, content);
        if (recipientAccessLevel != null) {
            log.debug("User {} has recipient access level {} to content {}", 
                    userId, recipientAccessLevel, content.getId());
            return recipientAccessLevel;
        }

        // Default access based on privacy level and generation
        LegacyRecipient.AccessLevel defaultAccessLevel = getDefaultAccessLevel(content.getPrivacyLevel(), 
                content.getGenerationLevel(), userGenerationLevel);
        
        log.debug("User {} has default access level {} to content {} based on privacy and generation", 
                userId, defaultAccessLevel, content.getId());
        
        return defaultAccessLevel;
    }

    @Override
    public boolean canInheritContent(LegacyContent content, Integer targetGenerationLevel) {
        log.debug("Checking if content {} can be inherited by generation {}", 
                content.getId(), targetGenerationLevel);

        // Content can be inherited if:
        // 1. It's not private
        // 2. The target generation is appropriate for the content's generation level
        // 3. Sharing is allowed between the generations

        if (content.getPrivacyLevel() == LegacyContent.PrivacyLevel.PRIVATE) {
            log.debug("Content {} cannot be inherited due to PRIVATE privacy level", content.getId());
            return false;
        }

        boolean sharingAllowed = isSharingAllowedBetweenGenerations(
                content.getGenerationLevel(), targetGenerationLevel);
        
        log.debug("Content {} inheritance to generation {}: {}", 
                content.getId(), targetGenerationLevel, sharingAllowed);
        
        return sharingAllowed;
    }

    @Override
    public List<UUID> getAccessibleUsers(LegacyContent content, List<UUID> familyMembers, 
                                       List<UUID> extendedFamilyMembers) {
        log.debug("Getting accessible users for content: {} (privacy: {})", 
                content.getId(), content.getPrivacyLevel());

        List<UUID> accessibleUsers = new ArrayList<>();

        // Always include the creator
        accessibleUsers.add(content.getCreatorId());

        switch (content.getPrivacyLevel()) {
            case PUBLIC:
                // For public content, we can't determine all users, so return empty list
                // The calling service should handle this case
                log.debug("Content {} is PUBLIC - accessible to all users", content.getId());
                break;
                
            case EXTENDED_FAMILY:
                accessibleUsers.addAll(extendedFamilyMembers);
                // Fall through to include family members
                
            case FAMILY:
                accessibleUsers.addAll(familyMembers);
                break;
                
            case PRIVATE:
                // Only creator has access
                break;
        }

        // Add users with specific recipient access
        List<UUID> recipientUsers = getRecipientUsers(content);
        accessibleUsers.addAll(recipientUsers);

        log.debug("Content {} accessible to {} users", content.getId(), accessibleUsers.size());
        return accessibleUsers;
    }

    @Override
    public boolean validatePrivacyInheritance(LegacyContent.PrivacyLevel parentPrivacyLevel, LegacyContent.PrivacyLevel childPrivacyLevel) {
        log.debug("Validating privacy inheritance from {} to {}", parentPrivacyLevel, childPrivacyLevel);

        // Privacy inheritance rules:
        // - Child privacy level cannot be more restrictive than parent
        // - PRIVATE -> any level allowed
        // - FAMILY -> FAMILY, EXTENDED_FAMILY, PUBLIC allowed
        // - EXTENDED_FAMILY -> EXTENDED_FAMILY, PUBLIC allowed
        // - PUBLIC -> PUBLIC only

        boolean isValid = parentPrivacyLevel.ordinal() <= childPrivacyLevel.ordinal();
        
        log.debug("Privacy inheritance from {} to {}: {}", parentPrivacyLevel, childPrivacyLevel, isValid);
        return isValid;
    }

    @Override
    public LegacyContent.PrivacyLevel getDefaultPrivacyLevelForGeneration(Integer generationLevel) {
        log.debug("Getting default privacy level for generation: {}", generationLevel);

        // Generation-based default privacy levels:
        // - Generation 0 (Grandparents): FAMILY (more open)
        // - Generation 1 (Parents): FAMILY (balanced)
        // - Generation 2 (Children): EXTENDED_FAMILY (more restrictive)
        // - Generation 3+ (Grandchildren): PRIVATE (most restrictive)

        LegacyContent.PrivacyLevel defaultPrivacy;
        if (generationLevel == null || generationLevel <= 0) {
            defaultPrivacy = LegacyContent.PrivacyLevel.FAMILY;
        } else if (generationLevel == 1) {
            defaultPrivacy = LegacyContent.PrivacyLevel.FAMILY;
        } else if (generationLevel == 2) {
            defaultPrivacy = LegacyContent.PrivacyLevel.EXTENDED_FAMILY;
        } else {
            defaultPrivacy = LegacyContent.PrivacyLevel.PRIVATE;
        }

        log.debug("Default privacy level for generation {}: {}", generationLevel, defaultPrivacy);
        return defaultPrivacy;
    }

    @Override
    public boolean canModifyPrivacyLevel(UUID userId, LegacyContent content, LegacyContent.PrivacyLevel newPrivacyLevel) {
        log.debug("Checking if user {} can modify privacy level of content {} to {}", 
                userId, content.getId(), newPrivacyLevel);

        // Only the creator can modify privacy level
        boolean isCreator = content.getCreatorId().equals(userId);
        if (!isCreator) {
            log.debug("User {} cannot modify privacy level - not the creator", userId);
            return false;
        }

        // Validate privacy inheritance if this content inherits from another
        // TODO: Implement parent content privacy validation
        
        log.debug("User {} can modify privacy level of content {} to {}", 
                userId, content.getId(), newPrivacyLevel);
        return true;
    }

    @Override
    public boolean isContentVisibleToGeneration(Integer contentGenerationLevel, Integer userGenerationLevel) {
        log.debug("Checking content visibility: content generation {} to user generation {}", 
                contentGenerationLevel, userGenerationLevel);

        // Content visibility rules:
        // - Content is visible to same generation and all descendants (no generational limits)
        // - Content is visible to one generation up (parents)
        // - Content is not visible to generations more than one level up (grandparents)
        // - Allows grandparents' content to be visible to great-grandchildren, etc.

        if (contentGenerationLevel == null || userGenerationLevel == null) {
            log.debug("Null generation levels - allowing visibility");
            return true;
        }

        boolean isVisible = (userGenerationLevel >= contentGenerationLevel - 1);

        log.debug("Content generation {} visible to user generation {}: {}", 
                contentGenerationLevel, userGenerationLevel, isVisible);
        return isVisible;
    }

    @Override
    public boolean isSharingAllowedBetweenGenerations(Integer fromGeneration, Integer toGeneration) {
        log.debug("Checking sharing permission from generation {} to generation {}", 
                fromGeneration, toGeneration);

        if (fromGeneration == null || toGeneration == null) {
            log.debug("Null generation levels - allowing sharing");
            return true;
        }

        // Sharing rules:
        // - Can share to any generation within the family
        // - No artificial limits on generational distance
        // - Sharing is controlled by privacy settings and family relationships
        // - Allows grandparents to share with great-grandchildren, etc.

        boolean isAllowed = true; // No generational limits - let privacy and relationships control access

        log.debug("Sharing from generation {} to generation {}: {}", 
                fromGeneration, toGeneration, isAllowed);
        return isAllowed;
    }

    // ==============================================
    // PRIVATE HELPER METHODS
    // ==============================================

    private boolean hasRecipientAccess(UUID userId, LegacyContent content) {
        if (content.getRecipients() == null) {
            return false;
        }

        return content.getRecipients().stream()
                .anyMatch(recipient -> recipient.getRecipientId().equals(userId) && 
                         recipient.getStatus() == LegacyRecipient.RecipientStatus.ACCEPTED);
    }

    private LegacyRecipient.AccessLevel getRecipientAccessLevel(UUID userId, LegacyContent content) {
        if (content.getRecipients() == null) {
            return null;
        }

        return content.getRecipients().stream()
                .filter(recipient -> recipient.getRecipientId().equals(userId) && 
                        recipient.getStatus() == LegacyRecipient.RecipientStatus.ACCEPTED)
                .map(LegacyRecipient::getAccessLevel)
                .findFirst()
                .orElse(null);
    }

    private List<UUID> getRecipientUsers(LegacyContent content) {
        if (content.getRecipients() == null) {
            return new ArrayList<>();
        }

        return content.getRecipients().stream()
                .filter(recipient -> recipient.getStatus() == LegacyRecipient.RecipientStatus.ACCEPTED)
                .map(LegacyRecipient::getRecipientId)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private LegacyRecipient.AccessLevel getDefaultAccessLevel(LegacyContent.PrivacyLevel privacyLevel, Integer contentGenerationLevel, 
                                            Integer userGenerationLevel) {
        // Default access levels based on privacy and generation:
        // - PRIVATE: No access (handled by hasContentAccess)
        // - FAMILY: READ access for family members
        // - EXTENDED_FAMILY: READ access for extended family
        // - PUBLIC: READ access for everyone

        switch (privacyLevel) {
            case FAMILY:
            case EXTENDED_FAMILY:
            case PUBLIC:
                return LegacyRecipient.AccessLevel.READ;
            default:
                return LegacyRecipient.AccessLevel.READ; // Fallback
        }
    }
}
